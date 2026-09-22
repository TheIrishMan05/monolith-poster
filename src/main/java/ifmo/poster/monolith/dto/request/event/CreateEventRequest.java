package ifmo.poster.monolith.dto.request.event;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class CreateEventRequest {
    @NotBlank
    @Size(min = 5, max = 100)
    private String eventName;

    @NotBlank
    @Size(min = 50, max = 200)
    private String description;

    @NotBlank
    @Size(min = 5, max = 200)
    private String location;

    @NotNull
    @FutureOrPresent
    private LocalDateTime dateTime;

    private Set<String> tagNames; 
}