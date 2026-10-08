package ifmo.poster.monolith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class TicketWaitlistIntegrationTest extends AbstractIntegrationTest {

    @Test
    void joinRequiresSoldOutInventoryAndReopensSameRowAfterCancel() throws Exception {
        Long userId = createUser();
        Long eventId = createActiveEvent("Waitlist Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(asAdmin(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 1
                        )))))
                .andExpect(status().isCreated());

        mockMvc.perform(asUser(post("/api/ticket-waitlist")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))), userId))
                .andExpect(status().isConflict());

        mockMvc.perform(asUser(post("/api/orders")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "userId", userId,
                                "items", List.of(item(eventId, ticketTypeId, 1))
                        ))), userId))
                .andExpect(status().isCreated());

        MvcResult joinWhenSoldOut = mockMvc.perform(asUser(post("/api/ticket-waitlist")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))), userId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andReturn();
        Long waitlistId = idFrom(joinWhenSoldOut);

        mockMvc.perform(asUser(post("/api/ticket-waitlist")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))), userId))
                .andExpect(status().isConflict());

        mockMvc.perform(asUser(post("/api/ticket-waitlist/{id}/cancel", waitlistId), userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(asUser(post("/api/ticket-waitlist")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))), userId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(waitlistId))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void cancelForeignWaitlistEntryLooksLikeNotFound() throws Exception {
        Long ownerId = createUser();
        Long strangerId = createUser();
        Long eventId = createActiveEvent("Waitlist Privacy " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        MvcResult joinResult = mockMvc.perform(asUser(post("/api/ticket-waitlist")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))), ownerId))
                .andExpect(status().isCreated())
                .andReturn();
        Long waitlistId = idFrom(joinResult);

        mockMvc.perform(asUser(post("/api/ticket-waitlist/{id}/cancel", waitlistId), strangerId))
                .andExpect(status().isNotFound());

        mockMvc.perform(asUser(post("/api/ticket-waitlist/{id}/cancel", waitlistId), ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
