package ifmo.poster.monolith.dto.response.ticket;

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
public class TicketInventoryResponse {
    private Long eventId;
    private Long ticketTypeId;
    private int createdCount;
    private long availableTotal;
}