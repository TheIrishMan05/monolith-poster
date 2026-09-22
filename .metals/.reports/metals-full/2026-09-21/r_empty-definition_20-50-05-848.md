error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/OrderItem.java:_empty_/Getter#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/OrderItem.java
empty definition using pc, found symbol in pc: _empty_/Getter#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 442
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/OrderItem.java
text:
```scala
package ifmo.poster.monolith.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@Entity 
@Table (name = "order_items")
@Get@@ter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private TicketType ticketType;

    @Min(value = 1, message = "Должен заказан быть хотя бы один билет")
    private int quantity;

    @Positive
    private BigDecimal price;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Getter#