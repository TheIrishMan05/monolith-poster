error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Ticket.java:jakarta/persistence/Enumerated#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Ticket.java
empty definition using pc, found symbol in pc: jakarta/persistence/Enumerated#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 249
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Ticket.java
text:
```scala
package ifmo.poster.monolith.entity;

import java.math.BigDecimal;

import ifmo.poster.monolith.entity.Event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enum@@erated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name = "tickets")
@Getter 
@Setter
public class Ticket {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Event event;

  @OneToMany
  TicketType type;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: jakarta/persistence/Enumerated#