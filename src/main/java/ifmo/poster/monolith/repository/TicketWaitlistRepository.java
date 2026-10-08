package ifmo.poster.monolith.repository;

import ifmo.poster.monolith.entity.TicketWaitlist;
import ifmo.poster.monolith.enums.WaitlistStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketWaitlistRepository extends JpaRepository<TicketWaitlist, Long> {

    Optional<TicketWaitlist> findByUserIdAndEventIdAndTicketTypeIdAndStatus(
            Long userId,
            Long eventId,
            Long ticketTypeId,
            WaitlistStatus status
    );

    List<TicketWaitlist> findByEventIdAndTicketTypeIdAndStatus(
            Long eventId,
            Long ticketTypeId,
            WaitlistStatus status
    );

    Page<TicketWaitlist> findByUserId(Long userId, Pageable pageable);
}
