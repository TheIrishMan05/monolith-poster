error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Event.java:_empty_/Size#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Event.java
empty definition using pc, found symbol in pc: _empty_/Size#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 601
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Event.java
text:
```scala
package ifmo.poster.monolith.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
   @Column(nullable = false, unique = true)
   private String eventName;
   @Column(nullable = false)
   @Siz@@e(min = 50, max = 200)
   private String description;
   @Column(nullable = false)
   private String location;
   @Column(nullable = false)
   private LocalDateTime dateTime;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Size#