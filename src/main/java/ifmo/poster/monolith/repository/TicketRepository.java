package ifmo.poster.monolith.repository;

import ifmo.poster.monolith.entity.Ticket;
import ifmo.poster.monolith.enums.TicketStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    long countByEventIdAndTicketTypeIdAndStatus(
            Long eventId,
            Long ticketTypeId,
            TicketStatus status
    );

    List<Ticket> findByOrderItem_Order_Id(Long orderId);
}