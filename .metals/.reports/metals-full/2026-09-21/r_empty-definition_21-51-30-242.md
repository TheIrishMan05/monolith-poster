error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Seat.java:_empty_/Id#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Seat.java
empty definition using pc, found symbol in pc: _empty_/Id#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 390
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Seat.java
text:
```scala
package ifmo.poster.monolith.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seats")
@Getter 
@Setter 
public class Seat {
    @I@@d
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String row;
    private int number;

    @OneToOne
    @JoinColumn(name = "ticket_id", unique = true) 
    private Ticket ticket;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Id#