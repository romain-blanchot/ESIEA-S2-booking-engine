package bookingengine.domain.events;

import java.time.Instant;
import java.time.LocalDate;

public record ReservationCreatedEvent(
        Long reservationId,
        Long chambreId,
        Long utilisateurId,
        LocalDate dateDebut,
        LocalDate dateFin,
        String status,
        Instant timestamp
) {
    public static ReservationCreatedEvent of(Long reservationId, Long chambreId, Long utilisateurId, LocalDate dateDebut, LocalDate dateFin, String status) {
        return new ReservationCreatedEvent(reservationId, chambreId, utilisateurId, dateDebut, dateFin, status, Instant.now());
    }
}
