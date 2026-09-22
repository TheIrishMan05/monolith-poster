error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Event.java:_empty_/Column#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Event.java
empty definition using pc, found symbol in pc: _empty_/Column#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 463
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Event.java
text:
```scala
package ifmo.poster.monolith.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "events")
@Getter
@Setter
public class Event {
   @Id 
   @GeneratedValue (strategy = GenerationType.IDENTITY)
   private Long id;
   @Co@@lumn(nullable = false)
   private String name;
   private String description;
   private String location;
   private LocalDateTime dateTime;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Column#