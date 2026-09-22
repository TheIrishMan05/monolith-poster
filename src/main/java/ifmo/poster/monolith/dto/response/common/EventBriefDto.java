package ifmo.poster.monolith.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter 
@Setter
@Builder  
@NoArgsConstructor 
@AllArgsConstructor  
public class EventBriefDto {
    private Long id;
    private String eventName;
    private LocalDateTime dateTime;
    private String location;
}
