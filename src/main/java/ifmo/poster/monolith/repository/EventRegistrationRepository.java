package ifmo.poster.monolith.repository;

import ifmo.poster.monolith.entity.EventRegistration;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    Optional<EventRegistration> findByUserIdAndEventId(Long userId, Long eventId);

    Page<EventRegistration> findByUserId(Long userId, Pageable pageable);

    Page<EventRegistration> findByEventId(Long eventId, Pageable pageable);
}