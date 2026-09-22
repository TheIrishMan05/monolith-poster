error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/dto/request/ModerateEventRequest.java:_empty_/NotNull#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/dto/request/ModerateEventRequest.java
empty definition using pc, found symbol in pc: _empty_/NotNull#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 181
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/dto/request/ModerateEventRequest.java
text:
```scala
package ifmo.poster.monolith.dto.request;

import ifmo.poster.monolith.enums.EventStatus;
import jakarta.validation.constraints.Size;

public class ModerateEventRequest {
  
  @NotN@@ull(message="")
  private EventStatus newStatus;

  @Size(max = 500)
  private String reason;
  
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/NotNull#