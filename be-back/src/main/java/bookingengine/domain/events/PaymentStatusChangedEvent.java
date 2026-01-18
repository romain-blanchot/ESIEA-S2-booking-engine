package bookingengine.domain.events;

import java.time.Instant;

public record PaymentStatusChangedEvent(
        Long paymentId,
        String oldStatus,
        String newStatus,
        Instant timestamp
) {
    public static PaymentStatusChangedEvent of(Long paymentId, String oldStatus, String newStatus) {
        return new PaymentStatusChangedEvent(paymentId, oldStatus, newStatus, Instant.now());
    }
}
