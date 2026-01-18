package bookingengine.domain.events;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentCreatedEvent(
        Long paymentId,
        Long reservationId,
        BigDecimal amount,
        String paymentMethod,
        String status,
        Instant timestamp
) {
    public static PaymentCreatedEvent of(Long paymentId, Long reservationId, BigDecimal amount, String paymentMethod, String status) {
        return new PaymentCreatedEvent(paymentId, reservationId, amount, paymentMethod, status, Instant.now());
    }
}
