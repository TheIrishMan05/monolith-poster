package ifmo.poster.monolith.repository;

import ifmo.poster.monolith.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
  
}
