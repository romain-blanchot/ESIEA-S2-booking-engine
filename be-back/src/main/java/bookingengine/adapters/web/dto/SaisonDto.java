package bookingengine.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Représentation d'une saison tarifaire")
public record SaisonDto(
        @Schema(description = "Identifiant unique de la saison", example = "1")
        Long id,

        @Schema(description = "Nom de la saison", example = "Haute Saison Été")
        String nom,

        @Schema(description = "Date de début de la saison", example = "2024-06-01")
        LocalDate dateDebut,

        @Schema(description = "Date de fin de la saison", example = "2024-08-31")
        LocalDate dateFin,

        @Schema(description = "Coefficient multiplicateur du prix (1.0 = prix normal, 1.5 = +50%)", example = "1.5")
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
