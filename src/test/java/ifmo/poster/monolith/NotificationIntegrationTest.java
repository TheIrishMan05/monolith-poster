package ifmo.poster.monolith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ifmo.poster.monolith.entity.Notification;
import ifmo.poster.monolith.entity.User;
import ifmo.poster.monolith.repository.NotificationRepository;
import ifmo.poster.monolith.service.NotificationService;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;

class NotificationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void ownershipIsolatesDeleteAndDeleteAllIsScopedToUser() throws Exception {
        Long ownerId = createUser();
        Long strangerId = createUser();
        Long eventId = createActiveEvent("Notif Ownership " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(asUser(post("/api/ticket-waitlist")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))), ownerId))
                .andExpect(status().isCreated());

        mockMvc.perform(asAdmin(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 1
                        )))))
                .andExpect(status().isCreated());

        MvcResult list = mockMvc.perform(asUser(get("/api/notifications").param("size", "10"), ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andReturn();
        Long notificationId = bodyFrom(list).get("content").get(0).get("id").asLong();

        mockMvc.perform(asUser(delete("/api/notifications/{id}", notificationId), strangerId))
                .andExpect(status().isNotFound());

        mockMvc.perform(asUser(post("/api/notifications/{id}/read", notificationId), strangerId))
                .andExpect(status().isNotFound());

        User stranger = userRepository.findById(strangerId).orElseThrow();
        Notification strangerNote = notificationRepository.save(
                new Notification(stranger, "Hello", "Private message"));

        mockMvc.perform(asUser(delete("/api/notifications"), ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(1));

        mockMvc.perform(asUser(get("/api/notifications").param("size", "10"), ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        mockMvc.perform(asUser(get("/api/notifications").param("size", "10"), strangerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(strangerNote.getId()));
    }

    @Test
    void cleanupExpiredRemovesOnlyOldNotifications() throws Exception {
        Long userId = createUser();
        User user = userRepository.findById(userId).orElseThrow();

        Notification fresh = notificationRepository.save(new Notification(user, "Fresh", "Keep me"));
        Notification stale = notificationRepository.save(new Notification(user, "Stale", "Drop me"));

        transactionTemplate.executeWithoutResult(status -> {
            entityManager.createNativeQuery(
                            "UPDATE notifications SET created_at = :createdAt WHERE id = :id")
                    .setParameter("createdAt", LocalDateTime.now().minusDays(10))
                    .setParameter("id", stale.getId())
                    .executeUpdate();
        });

        notificationService.cleanupExpired();

        org.junit.jupiter.api.Assertions.assertTrue(
                notificationRepository.findById(fresh.getId()).isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(
                notificationRepository.findById(stale.getId()).isEmpty());
    }
}
