package ifmo.poster.monolith.repository;

import ifmo.poster.monolith.entity.Event;
import ifmo.poster.monolith.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    Slice<Event> findByStatusOrderByIdAsc(EventStatus status, Pageable pageable);

    Slice<Event> findByStatusAndIdGreaterThanOrderByIdAsc(
            EventStatus status,
            Long id,
            Pageable pageable
    );
}