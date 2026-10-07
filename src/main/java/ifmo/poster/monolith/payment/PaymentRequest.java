package ifmo.poster.monolith.payment;

import java.math.BigDecimal;

public record PaymentRequest(Long orderId, Long userId, BigDecimal amount) {
}