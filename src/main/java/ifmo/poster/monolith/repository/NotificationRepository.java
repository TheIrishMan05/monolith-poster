package ifmo.poster.monolith.repository;

import ifmo.poster.monolith.entity.Notification;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long deleteByCreatedAtBefore(LocalDateTime cutoff);

    long deleteByUserId(Long userId);
}
