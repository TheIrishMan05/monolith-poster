package ifmo.poster.monolith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ifmo.poster.monolith.payment.FakePaymentService;
import ifmo.poster.monolith.payment.PaymentRequest;
import ifmo.poster.monolith.payment.PaymentResult;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Откат транзакции заказа при отказе оплаты: билеты снова AVAILABLE, подтверждённого заказа нет.
 */
class OrderPaymentFailureIntegrationTest extends AbstractIntegrationTest {

    @MockitoBean
    private FakePaymentService paymentService;

    /** FakePaymentService возвращает fail → 409 и инвентарь не «залипает» в SOLD. */
    @Test
    void paymentFailureRollsBackSoldTickets() throws Exception {
        when(paymentService.pay(any(PaymentRequest.class)))
                .thenReturn(new PaymentResult(false, null, "card declined"));

        Long userId = createUser();
        Long eventId = createActiveEvent("Payment Fail Event " + System.nanoTime());
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
                                "items", List.of(item(eventId, ticketTypeId, 1))
                        ))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Payment failed")));

        mockMvc.perform(get("/api/tickets")
                        .param("eventId", String.valueOf(eventId))
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        mockMvc.perform(get("/api/orders")
                        .param("userId", String.valueOf(userId))
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}
