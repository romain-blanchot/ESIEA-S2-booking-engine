package bookingengine.domain.events;

import java.time.Instant;
import java.time.LocalDate;

public record SaisonCreatedEvent(
        Long saisonId,
        String nom,
        LocalDate dateDebut,
        LocalDate dateFin,
        double coefficientPrix,
        Instant timestamp
) {
    public static SaisonCreatedEvent of(Long saisonId, String nom, LocalDate dateDebut, LocalDate dateFin, double coefficientPrix) {
        return new SaisonCreatedEvent(saisonId, nom, dateDebut, dateFin, coefficientPrix, Instant.now());
    }
}
