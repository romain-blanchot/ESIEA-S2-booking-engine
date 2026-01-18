package bookingengine.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Requête de calcul de prix pour un séjour")
public record CalculPrixRequest(
        @Schema(description = "Identifiant de la chambre", example = "1", required = true)
        Long chambreId,

        @Schema(description = "Date d'arrivée (check-in)", example = "2024-07-15", required = true)
        LocalDate dateDebut,

        @Schema(description = "Date de départ (check-out)", example = "2024-07-20", required = true)
        LocalDate dateFin
) {}
