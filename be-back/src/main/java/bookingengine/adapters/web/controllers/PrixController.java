package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.CalculPrixRequest;
import bookingengine.usecase.prix.CalculPrixUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prix")
@Tag(name = "Prix", description = "Calcul des prix")
public class PrixController {

    private final CalculPrixUseCase calculPrixUseCase;

    public PrixController(CalculPrixUseCase calculPrixUseCase) {
        this.calculPrixUseCase = calculPrixUseCase;
    }

    @PostMapping("/calculer")
    @Operation(summary = "Calculer le prix d'un séjour")
    public ResponseEntity<CalculPrixUseCase.ResultatCalculPrix> calculerPrix(@RequestBody CalculPrixRequest request) {
        CalculPrixUseCase.ResultatCalculPrix resultat = calculPrixUseCase.calculerPrixDetaille(
                request.chambreId(),
                request.dateDebut(),
                request.dateFin()
        );
        return ResponseEntity.ok(resultat);
    }
}
