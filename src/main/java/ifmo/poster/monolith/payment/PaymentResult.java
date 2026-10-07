package ifmo.poster.monolith.payment;

public record PaymentResult(boolean success, String transactionId, String message) {
}