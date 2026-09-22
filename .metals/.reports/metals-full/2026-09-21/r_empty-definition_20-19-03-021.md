error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/TicketType.java:_empty_/Size#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/TicketType.java
empty definition using pc, found symbol in pc: _empty_/Size#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 496
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/TicketType.java
text:
```scala
package ifmo.poster.monolith.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "ticket_types")
public class TicketType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Siz@@e(min = 3, max = 50)
    
    private String typeName;
    
    @Positive 
    private BigDecimal price;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Size#