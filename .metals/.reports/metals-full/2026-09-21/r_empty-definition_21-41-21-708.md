error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Ticket.java:_empty_/Set#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Ticket.java
empty definition using pc, found symbol in pc: _empty_/Set#
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 934
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Ticket.java
text:
```scala
package ifmo.poster.monolith.entity;

import java.util.HashSet;

import ifmo.poster.monolith.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tickets")
@Getter
@Setter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    @ManyToMany
  @ JoinTable(
      name = "event_tags", 
      joinColumns = @JoinColumn(name = "event_id"), 
      inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private S@@et<Tag> tags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Set#