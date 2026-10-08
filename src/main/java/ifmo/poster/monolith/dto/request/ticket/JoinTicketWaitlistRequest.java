package ifmo.poster.monolith.dto.request.ticket;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinTicketWaitlistRequest {

    @NotNull(message = "ID события обязательно")
    private Long eventId;

    @NotNull(message = "ID типа билета обязательно")
    private Long ticketTypeId;
}
