package ifmo.poster.monolith.dto.response.event;

import ifmo.poster.monolith.dto.response.common.TicketTypeAvailabilityDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PublicEventDetailResponse {
    private Long id;
    private String eventName;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private Set<String> tags;
    private List<TicketTypeAvailabilityDto> availableTickets;
}