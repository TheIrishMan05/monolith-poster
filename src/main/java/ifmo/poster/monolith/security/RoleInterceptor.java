package ifmo.poster.monolith.security;

import ifmo.poster.monolith.entity.User;
import ifmo.poster.monolith.enums.Role;
import ifmo.poster.monolith.exception.AccessDeniedException;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RoleInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRoles requireRoles = handlerMethod.getMethodAnnotation(RequireRoles.class);
        if (requireRoles == null) {
            requireRoles = handlerMethod.getBeanType().getAnnotation(RequireRoles.class);
        }
        if (requireRoles == null) {
            return true;
        }

        String userIdParam = request.getParameter("userId");
        String roleParam = request.getParameter("role");
        if (userIdParam == null || userIdParam.isBlank() || roleParam == null || roleParam.isBlank()) {
            throw new AccessDeniedException("Query params userId and role are required");
        }

        long userId;
        try {
            userId = Long.parseLong(userIdParam);
        } catch (NumberFormatException ex) {
            throw new AccessDeniedException("Invalid userId");
        }

        Role claimedRole;
        try {
            claimedRole = Role.valueOf(roleParam.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AccessDeniedException("Invalid role: " + roleParam);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getRole() != claimedRole) {
            throw new AccessDeniedException(
                    "Claimed role " + claimedRole + " does not match user role " + user.getRole());
        }

        boolean allowed = Arrays.stream(requireRoles.value())
                .anyMatch(required -> required == claimedRole);
        if (!allowed) {
            throw new AccessDeniedException(
                    "Role " + claimedRole + " is not allowed for this operation");
        }

        RoleContext.setUser(user);
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        RoleContext.clear();
    }
}
