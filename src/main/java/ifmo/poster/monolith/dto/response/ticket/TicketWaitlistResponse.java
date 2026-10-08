package ifmo.poster.monolith.dto.response.ticket;

import ifmo.poster.monolith.enums.WaitlistStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketWaitlistResponse {
    private Long id;
    private Long userId;
    private Long eventId;
    private String eventName;
    private Long ticketTypeId;
    private String ticketTypeName;
    private WaitlistStatus status;
    private LocalDateTime createdAt;
}
