error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/dto/request/event/CreateEventRequest.java:Setter#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/dto/request/event/CreateEventRequest.java
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 167
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/dto/request/event/CreateEventRequest.java
text:
```scala
package ifmo.poster.monolith.dto.request.event;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter@@
public class CreateEventRequest {
    @NotBlank
    @Size(min = 5, max = 100)
    private String eventName;

    @NotBlank
    @Size(min = 50, max = 200)
    private String description;

    @NotBlank
    @Size(min = 5, max = 200)
    private String location;

    @NotNull
    @FutureOrPresent
    private LocalDateTime dateTime;

    private Set<String> tagNames; 
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 