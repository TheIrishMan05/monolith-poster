error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Order.java:_empty_/OrderStatus#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Order.java
empty definition using pc, found symbol in pc: _empty_/OrderStatus#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1046
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Order.java
text:
```scala
package ifmo.poster.monolith.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders") 
@Getter 
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderS@@tatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/OrderStatus#