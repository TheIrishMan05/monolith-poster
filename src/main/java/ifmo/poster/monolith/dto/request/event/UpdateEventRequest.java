package ifmo.poster.monolith.dto.request.event;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEventRequest {
    @NotBlank
    @Size(min = 5, max = 100)
    private String eventName;

    @NotBlank
    @Size(min = 50, max = 200)
    private String description;

    @NotBlank
    @Size(min = 5, max = 200)
    private String location;
    
    private LocalDateTime dateTime; 
}