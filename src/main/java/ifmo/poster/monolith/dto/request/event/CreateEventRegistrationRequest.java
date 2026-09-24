package ifmo.poster.monolith.dto.request.event;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEventRegistrationRequest {

    @NotNull(message = "ID события обязательно")
    private Long eventId;

}