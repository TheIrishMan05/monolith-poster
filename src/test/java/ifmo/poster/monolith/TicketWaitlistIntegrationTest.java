package ifmo.poster.monolith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Очередь на билеты: join только при sold-out, unique (user,event,type) переиспользует строку
 * после cancel, чужая запись выглядит как 404.
 */
class TicketWaitlistIntegrationTest extends AbstractIntegrationTest {

    /**
     * При наличии AVAILABLE — conflict; после покупки — WAITING;
     * повторный join — conflict; cancel → join снова с тем же id.
     */
    @Test
    void joinRequiresSoldOutInventoryAndReopensSameRowAfterCancel() throws Exception {
        Long userId = createUser();
        Long eventId = createActiveEvent("Waitlist Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 1
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/ticket-waitlist")
                        .param("userId", String.valueOf(userId))
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/orders")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "userId", userId,
                                "items", List.of(item(eventId, ticketTypeId, 1))
                        ))))
                .andExpect(status().isCreated());

        MvcResult joinWhenSoldOut = mockMvc.perform(post("/api/ticket-waitlist")
                        .param("userId", String.valueOf(userId))
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andReturn();
        Long waitlistId = idFrom(joinWhenSoldOut);

        mockMvc.perform(post("/api/ticket-waitlist")
                        .param("userId", String.valueOf(userId))
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/ticket-waitlist/{id}/cancel", waitlistId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(post("/api/ticket-waitlist")
                        .param("userId", String.valueOf(userId))
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(waitlistId))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    /** Cancel чужой waitlist-записи → 404 (без утечки существования), владелец может отменить. */
    @Test
    void cancelForeignWaitlistEntryLooksLikeNotFound() throws Exception {
        Long ownerId = createUser();
        Long strangerId = createUser();
        Long eventId = createActiveEvent("Waitlist Privacy " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        MvcResult joinResult = mockMvc.perform(post("/api/ticket-waitlist")
                        .param("userId", String.valueOf(ownerId))
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        Long waitlistId = idFrom(joinResult);

        mockMvc.perform(post("/api/ticket-waitlist/{id}/cancel", waitlistId)
                        .param("userId", String.valueOf(strangerId)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/ticket-waitlist/{id}/cancel", waitlistId)
                        .param("userId", String.valueOf(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
