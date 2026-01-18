package bookingengine.adapters.web.dto;

import java.time.LocalDate;

public record SaisonDto(
        Long id,
        String nom,
        LocalDate dateDebut,
        LocalDate dateFin,
        double coefficientPrix
) {
    public static SaisonDto from(bookingengine.domain.entities.Saison saison) {
        return new SaisonDto(
                saison.getId(),
                saison.getNom(),
                saison.getDateDebut(),
                saison.getDateFin(),
                saison.getCoefficientPrix()
        );
    }

    public bookingengine.domain.entities.Saison toDomain() {
        return new bookingengine.domain.entities.Saison(id, nom, dateDebut, dateFin, coefficientPrix);
    }
}
