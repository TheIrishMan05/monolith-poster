error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/EventParticipant.java:_empty_/Id#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/EventParticipant.java
empty definition using pc, found symbol in pc: _empty_/Id#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 480
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/EventParticipant.java
text:
```scala
package ifmo.poster.monolith.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "event_participants")
@Getter
@Setter
public class EventParticipant {
    @@@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate; 
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Id#