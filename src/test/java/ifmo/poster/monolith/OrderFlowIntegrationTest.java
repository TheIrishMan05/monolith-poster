package ifmo.poster.monolith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class OrderFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    void orderCreationSellsTicketsAndCancelMakesThemAvailableAgain() throws Exception {
        Long userId = createUser();
        Long eventId = createActiveEvent("Order Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 2
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdCount").value(2))
                .andExpect(jsonPath("$.availableTotal").value(2));

        MvcResult orderResult = mockMvc.perform(post("/api/orders")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "userId", userId,
                                "items", List.of(item(eventId, ticketTypeId, 2))
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.items[0].tickets.length()").value(2))
                .andReturn();

        Long orderId = idFrom(orderResult);

        mockMvc.perform(get("/api/tickets")
                        .param("eventId", String.valueOf(eventId))
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        mockMvc.perform(post("/api/orders/{id}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(get("/api/tickets")
                        .param("eventId", String.valueOf(eventId))
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void orderCreationRejectsUnavailableQuantity() throws Exception {
        Long userId = createUser();
        Long eventId = createActiveEvent("Limited Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 1
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/orders")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "userId", userId,
                                "items", List.of(item(eventId, ticketTypeId, 2))
                        ))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

}
