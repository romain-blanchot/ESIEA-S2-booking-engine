package bookingengine.adapters.web.dto;

import bookingengine.domain.entities.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long chambreId,
        Long utilisateurId,
        LocalDate dateDebut,
        LocalDate dateFin,
        String status,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getChambreId(),
                reservation.getUtilisateurId(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                reservation.getStatus().name(),
                reservation.getCreatedAt(),
                reservation.getCancelledAt()
        );
    }
}
