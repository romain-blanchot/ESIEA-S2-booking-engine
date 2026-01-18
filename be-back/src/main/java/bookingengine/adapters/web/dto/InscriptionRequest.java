package bookingengine.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requête d'inscription d'un nouvel utilisateur")
public record InscriptionRequest(
        @Schema(description = "Nom d'utilisateur unique", example = "jean.dupont", required = true)
        String username,

        @Schema(description = "Mot de passe (min 8 caractères recommandé)", example = "MonMotDePasse123!", required = true)
        String password,

        @Schema(description = "Adresse email unique", example = "jean.dupont@email.com", required = true)
        String email
) {}
