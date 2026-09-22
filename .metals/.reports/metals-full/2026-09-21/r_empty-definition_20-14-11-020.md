error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/OrderItem.java:_empty_/Min#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/OrderItem.java
empty definition using pc, found symbol in pc: _empty_/Min#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 553
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/OrderItem.java
text:
```scala
package ifmo.poster.monolith.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Positive;

public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private TicketType ticketType;

    @Positive
    @M@@in(value = 1, message = "Должен заказан быть хотя бы один билет")
    private int quantity;

    @Positive
    private BigDecimal price;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Min#