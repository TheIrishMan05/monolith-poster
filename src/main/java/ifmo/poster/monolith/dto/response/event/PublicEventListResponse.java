package ifmo.poster.monolith.dto.response.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PublicEventListResponse {
    private Long id;
    private String eventName;
    private LocalDateTime dateTime;
    private String location;
    private BigDecimal minPrice; 
    private Set<String> tags;
}