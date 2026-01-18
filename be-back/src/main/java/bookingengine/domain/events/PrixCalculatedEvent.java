package bookingengine.domain.events;

import java.time.Instant;
import java.time.LocalDate;

public record PrixCalculatedEvent(
        Long chambreId,
        String numeroChambre,
        String typeChambre,
        LocalDate dateDebut,
        LocalDate dateFin,
        long nombreNuits,
        double prixTotal,
        Instant timestamp
) {
    public static PrixCalculatedEvent of(Long chambreId, String numeroChambre, String typeChambre,
                                          LocalDate dateDebut, LocalDate dateFin, long nombreNuits, double prixTotal) {
        return new PrixCalculatedEvent(chambreId, numeroChambre, typeChambre, dateDebut, dateFin, nombreNuits, prixTotal, Instant.now());
    }
}
