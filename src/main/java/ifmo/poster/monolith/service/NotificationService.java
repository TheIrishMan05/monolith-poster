package ifmo.poster.monolith.service;

import ifmo.poster.monolith.dto.response.notification.NotificationResponse;
import ifmo.poster.monolith.entity.Notification;
import ifmo.poster.monolith.entity.TicketWaitlist;
import ifmo.poster.monolith.entity.User;
import ifmo.poster.monolith.enums.WaitlistStatus;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.notification.EmailNotificationService;
import ifmo.poster.monolith.repository.NotificationRepository;
import ifmo.poster.monolith.repository.TicketWaitlistRepository;
import ifmo.poster.monolith.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final TicketWaitlistRepository waitlistRepository;
    private final UserRepository userRepository;
    private final EmailNotificationService emailNotificationService;
    private final int retentionDays;

    public NotificationService(
            NotificationRepository notificationRepository,
            TicketWaitlistRepository waitlistRepository,
            UserRepository userRepository,
            EmailNotificationService emailNotificationService,
            @Value("${app.notifications.retention-days:7}") int retentionDays
    ) {
        this.notificationRepository = notificationRepository;
        this.waitlistRepository = waitlistRepository;
        this.userRepository = userRepository;
        this.emailNotificationService = emailNotificationService;
        this.retentionDays = retentionDays;
    }

    /**
     * Оповещает всех WAITING по event+ticketType (in-app + email stub).
     */
    @Transactional
    public void notifyTicketAvailability(Long eventId, Long ticketTypeId) {
        List<TicketWaitlist> waiting = waitlistRepository.findByEventIdAndTicketTypeIdAndStatus(
                eventId,
                ticketTypeId,
                WaitlistStatus.WAITING
        );
        if (waiting.isEmpty()) {
            return;
        }

        for (TicketWaitlist entry : waiting) {
            User user = entry.getUser();
            String eventName = entry.getEvent().getEventName();
            String typeName = entry.getTicketType().getTypeName();
            String title = "Tickets available";
            String message = "Tickets of type " + typeName
                    + " for event \"" + eventName + "\" are available again. You can place an order.";

            notificationRepository.save(new Notification(user, title, message));
            emailNotificationService.send(user.getEmail(), title, message);
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public NotificationResponse markRead(Long notificationId, Long userId) {
        Notification notification = findOwned(notificationId, userId);
        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void delete(Long notificationId, Long userId) {
        Notification notification = findOwned(notificationId, userId);
        notificationRepository.delete(notification);
    }

    @Transactional
    public long deleteAllForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return notificationRepository.deleteByUserId(userId);
    }

    /**
     * Каждый день в 03:00 удаляет in-app уведомления старше retention-days.
     */
    @Scheduled(cron = "${app.notifications.cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void cleanupExpired() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        long removed = notificationRepository.deleteByCreatedAtBefore(cutoff);
        if (removed > 0) {
            log.info("Removed {} notifications older than {} days", removed, retentionDays);
        }
    }

    private Notification findOwned(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found: " + notificationId));
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found: " + notificationId);
        }
        return notification;
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
