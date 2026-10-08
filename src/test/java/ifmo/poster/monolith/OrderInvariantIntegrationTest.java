package ifmo.poster.monolith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class OrderInvariantIntegrationTest extends AbstractIntegrationTest {

    @Test
    void concurrentOrdersCannotOversellSingleTicket() throws Exception {
        Long buyerOne = createUser();
        Long buyerTwo = createUser();
        Long eventId = createActiveEvent("Race Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(asAdmin(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 1
                        )))))
                .andExpect(status().isCreated());

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Callable<Integer>> tasks = List.of(
                    () -> createOrderStatus(buyerOne, eventId, ticketTypeId),
                    () -> createOrderStatus(buyerTwo, eventId, ticketTypeId)
            );
            List<Future<Integer>> futures = pool.invokeAll(tasks);
            List<Integer> statuses = new ArrayList<>();
            for (Future<Integer> future : futures) {
                statuses.add(future.get(30, TimeUnit.SECONDS));
            }

            long successCount = statuses.stream().filter(code -> code == 201).count();
            long conflictCount = statuses.stream().filter(code -> code == 409).count();
            assertEquals(1, successCount, "exactly one order must succeed: " + statuses);
            assertEquals(1, conflictCount, "the other order must conflict: " + statuses);
        } finally {
            pool.shutdownNow();
        }

        mockMvc.perform(asUser(get("/api/tickets")
                        .param("eventId", String.valueOf(eventId))
                        .param("status", "AVAILABLE"), buyerOne))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void cancelNotifiesWaitlistOnceAndDoubleCancelIsRejected() throws Exception {
        Long buyerId = createUser();
        Long waiterId = createUser();
        Long eventId = createActiveEvent("Cancel Notify Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(asAdmin(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 1
                        )))))
                .andExpect(status().isCreated());

        MvcResult orderResult = mockMvc.perform(asUser(post("/api/orders")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "userId", buyerId,
                                "items", List.of(item(eventId, ticketTypeId, 1))
                        ))), buyerId))
                .andExpect(status().isCreated())
                .andReturn();
        Long orderId = idFrom(orderResult);

        mockMvc.perform(asUser(post("/api/ticket-waitlist")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId
                        ))), waiterId))
                .andExpect(status().isCreated());

        mockMvc.perform(asUser(post("/api/orders/{id}/cancel", orderId), buyerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(asUser(get("/api/notifications").param("size", "10"), waiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Tickets available"));

        mockMvc.perform(asUser(post("/api/orders/{id}/cancel", orderId), buyerId))
                .andExpect(status().isConflict());

        mockMvc.perform(asUser(get("/api/notifications").param("size", "10"), waiterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        mockMvc.perform(asUser(get("/api/tickets")
                        .param("eventId", String.valueOf(eventId))
                        .param("status", "AVAILABLE"), buyerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    private int createOrderStatus(Long userId, Long eventId, Long ticketTypeId) throws Exception {
        MvcResult result = mockMvc.perform(asUser(post("/api/orders")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "userId", userId,
                                "items", List.of(item(eventId, ticketTypeId, 1))
                        ))), userId))
                .andReturn();
        return result.getResponse().getStatus();
    }
}
