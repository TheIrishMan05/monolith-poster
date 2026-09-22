package ifmo.poster.monolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ifmo.poster.monolith.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
  
}
