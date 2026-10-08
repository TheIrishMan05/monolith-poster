package ifmo.poster.monolith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class TicketInventoryIntegrationTest extends AbstractIntegrationTest {

    @Test
    void rejectsBatchAboveConfiguredLimitWithoutPersisting() throws Exception {
        Long eventId = createActiveEvent("Batch Limit Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();
        Long readerId = createUser();

        mockMvc.perform(asAdmin(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 201
                        )))))
                .andExpect(status().isConflict());

        mockMvc.perform(asUser(get("/api/tickets")
                        .param("eventId", String.valueOf(eventId))
                        .param("size", "10"), readerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void rejectsInconsistentAndDuplicateSeatSpecs() throws Exception {
        Long eventId = createActiveEvent("Seat Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(asAdmin(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 2,
                                "seats", List.of(Map.of("row", "A", "number", 1))
                        )))))
                .andExpect(status().isConflict());

        mockMvc.perform(asAdmin(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "seats", List.of(
                                        Map.of("row", "a", "number", 1),
                                        Map.of("row", "A", "number", 1)
                                )
                        )))))
                .andExpect(status().isConflict());
    }

    @Test
    void creatingInventoryNotifiesWaitingUsers() throws Exception {
        Long waiterId = createUser();
        Long eventId = createActiveEvent("Notify Stock Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(asUser(post("/api/ticket-waitlist")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))), waiterId))
                .andExpect(status().isCreated());

        mockMvc.perform(asAdmin(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 1
                        )))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdCount").value(1));

        MvcResult notifications = mockMvc.perform(asUser(get("/api/notifications")
                        .param("size", "10"), waiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Tickets available"))
                .andExpect(jsonPath("$.content[0].read").value(false))
                .andReturn();

        Long notificationId = bodyFrom(notifications).get("content").get(0).get("id").asLong();

        mockMvc.perform(asUser(post("/api/notifications/{id}/read", notificationId), waiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }
}
