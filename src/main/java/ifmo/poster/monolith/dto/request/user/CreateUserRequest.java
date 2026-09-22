package ifmo.poster.monolith.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "Username обязателен")
    @Size(min = 5, max = 50, message = "Длина username от 5 до 50 символов")
    private String userName;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;
    
}