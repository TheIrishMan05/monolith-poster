package ifmo.poster.monolith.payment;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FakePaymentService {

    public PaymentResult pay(PaymentRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentResult(false, null, "Invalid payment amount");
        }
        return new PaymentResult(true, "PAY-" + UUID.randomUUID(), "Payment accepted");
    }

    public PaymentResult refund(PaymentRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentResult(false, null, "Invalid refund amount");
        }
        return new PaymentResult(true, "REF-" + UUID.randomUUID(), "Refund accepted");
    }
}