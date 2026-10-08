package ifmo.poster.monolith.security;

import ifmo.poster.monolith.entity.User;
import ifmo.poster.monolith.enums.Role;

public final class RoleContext {

    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    private RoleContext() {
    }

    public static void setUser(User user) {
        CURRENT_USER.set(user);
    }

    public static User getUser() {
        return CURRENT_USER.get();
    }

    public static Long getUserId() {
        User user = CURRENT_USER.get();
        return user == null ? null : user.getId();
    }

    public static Role getRole() {
        User user = CURRENT_USER.get();
        return user == null ? null : user.getRole();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
