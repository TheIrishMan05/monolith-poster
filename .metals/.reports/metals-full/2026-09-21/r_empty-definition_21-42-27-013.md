error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Tag.java:_empty_/Set#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Tag.java
empty definition using pc, found symbol in pc: _empty_/Set#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 530
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/Tag.java
text:
```scala
package ifmo.poster.monolith.entity;

import java.util.HashSet;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tags")
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "tags")
    private Se@@t<Event> events = new HashSet<>();
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Set#