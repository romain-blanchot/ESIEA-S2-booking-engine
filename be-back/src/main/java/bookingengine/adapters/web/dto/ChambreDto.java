package bookingengine.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Représentation d'une chambre d'hôtel")
public record ChambreDto(
        @Schema(description = "Identifiant unique de la chambre", example = "1")
        Long id,

        @Schema(description = "Numéro de la chambre", example = "101")
        String numero,

        @Schema(description = "Type de chambre", example = "Double", allowableValues = {"Simple", "Double", "Suite", "Familiale"})
        String type,

        @Schema(description = "Prix de base par nuit en euros", example = "89.99")
        double prixBase,

        @Schema(description = "Capacité maximale de personnes", example = "2")
        int capacite,

        @Schema(description = "Description de la chambre", example = "Chambre double avec vue sur jardin, lit king-size, salle de bain privée")
        String description,

        @Schema(description = "Indique si la chambre est disponible à la réservation", example = "true")
        boolean disponible
) {
    public static ChambreDto from(bookingengine.domain.entities.Chambre chambre) {
        return new ChambreDto(
                chambre.getId(),
                chambre.getNumero(),
                chambre.getType(),
                chambre.getPrixBase(),
                chambre.getCapacite(),
                chambre.getDescription(),
                chambre.isDisponible()
        );
    }

    public bookingengine.domain.entities.Chambre toDomain() {
        return new bookingengine.domain.entities.Chambre(id, numero, type, prixBase, capacite, description, disponible);
    }
}
