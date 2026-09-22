package ifmo.poster.monolith.dto.response.ticket;

import ifmo.poster.monolith.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String eventName;
    private LocalDateTime eventDateTime;
    private String ticketTypeName; 
    private TicketStatus status;   
    private SeatInfo seat;        
    
    @Getter
    @Setter
    @AllArgsConstructor
    public static class SeatInfo {
        private String row;
        private Integer number;
    }
}
