package ifmo.poster.monolith.dto.request.user;

import ifmo.poster.monolith.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class UpdateRoleRequest {
    @NotNull(message = "Роль обязательна")
    private Role role;
}
