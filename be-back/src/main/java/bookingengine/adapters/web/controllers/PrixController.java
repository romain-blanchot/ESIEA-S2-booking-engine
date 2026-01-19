package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.CalculPrixRequest;
import bookingengine.usecase.prix.CalculPrixUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/prix")
@Tag(name = "Prix", description = "API de calcul des prix de séjour avec prise en compte des saisons tarifaires")
public class PrixController {

    private final CalculPrixUseCase calculPrixUseCase;

    public PrixController(CalculPrixUseCase calculPrixUseCase) {
        this.calculPrixUseCase = calculPrixUseCase;
    }

    @PostMapping("calculer")
    @Operation(
            summary = "Calculer le prix d'un séjour",
            description = "Calcule le prix total d'un séjour pour une chambre donnée entre deux dates, " +
                    "en appliquant les coefficients saisonniers jour par jour. Retourne le détail du calcul par nuit."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Calcul effectué avec succès",
                    content = @Content(schema = @Schema(implementation = CalculPrixUseCase.ResultatCalculPrix.class))),
            @ApiResponse(responseCode = "404", description = "Chambre non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dates invalides ou paramètres incorrects", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<CalculPrixUseCase.ResultatCalculPrix> calculerPrix(@RequestBody CalculPrixRequest request) {
        CalculPrixUseCase.ResultatCalculPrix resultat = calculPrixUseCase.calculerPrixDetaille(
                request.chambreId(),
                request.dateDebut(),
                request.dateFin()
        );
        return ResponseEntity.ok(resultat);
    }
}
