error id: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/User.java:_empty_/Role#
file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/User.java
empty definition using pc, found symbol in pc: _empty_/Role#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 591
uri: file:///C:/Users/grigo/study/highload_apps/monolith-poster/src/main/java/ifmo/poster/monolith/entity/User.java
text:
```scala
package ifmo.poster.monolith.entity;

import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Table(name = "users")
@Getter 
@Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String username;
  private String email;

  @Enumerated(EnumType.STRING)
  private Rol@@e role;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Role#