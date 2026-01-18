package bookingengine.domain.events;

import java.time.Instant;

public record ReservationCancelledEvent(
        Long reservationId,
        String reason,
        Instant timestamp
) {
    public static ReservationCancelledEvent of(Long reservationId, String reason) {
        return new ReservationCancelledEvent(reservationId, reason, Instant.now());
    }
}
