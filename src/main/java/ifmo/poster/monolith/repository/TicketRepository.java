package ifmo.poster.monolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ifmo.poster.monolith.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
  
}
