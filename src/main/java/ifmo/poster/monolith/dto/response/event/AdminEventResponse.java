package ifmo.poster.monolith.dto.response.event;

import ifmo.poster.monolith.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AdminEventResponse {
    private Long id;
    private String eventName;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private Set<String> tags;
    private EventStatus status;      
    private String blockReason;      
}