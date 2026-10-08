package ifmo.poster.monolith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Создание инвентаря: лимит batch (app.inventory.max-batch), валидация seats,
 * уведомление waitlist при появлении стока.
 */
class TicketInventoryIntegrationTest extends AbstractIntegrationTest {

    /** quantity &gt; max-batch → conflict, в БД билетов не появляется. */
    @Test
    void rejectsBatchAboveConfiguredLimitWithoutPersisting() throws Exception {
        Long eventId = createActiveEvent("Batch Limit Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 201
                        ))))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/tickets")
                        .param("eventId", String.valueOf(eventId))
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    /** quantity ≠ seats.size() и дубликат места (a/1 и A/1) отклоняются. */
    @Test
    void rejectsInconsistentAndDuplicateSeatSpecs() throws Exception {
        Long eventId = createActiveEvent("Seat Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 2,
                                "seats", List.of(Map.of("row", "A", "number", 1))
                        ))))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "seats", List.of(
                                        Map.of("row", "a", "number", 1),
                                        Map.of("row", "A", "number", 1)
                                )
                        ))))
                .andExpect(status().isConflict());
    }

    /** После create inventory waiting-пользователь получает unread-уведомление. */
    @Test
    void creatingInventoryNotifiesWaitingUsers() throws Exception {
        Long waiterId = createUser();
        Long eventId = createActiveEvent("Notify Stock Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(post("/api/ticket-waitlist")
                        .param("userId", String.valueOf(waiterId))
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 1
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdCount").value(1));

        MvcResult notifications = mockMvc.perform(get("/api/notifications")
                        .param("userId", String.valueOf(waiterId))
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Tickets available"))
                .andExpect(jsonPath("$.content[0].read").value(false))
                .andReturn();

        Long notificationId = bodyFrom(notifications).get("content").get(0).get("id").asLong();

        mockMvc.perform(post("/api/notifications/{id}/read", notificationId)
                        .param("userId", String.valueOf(waiterId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }
}
