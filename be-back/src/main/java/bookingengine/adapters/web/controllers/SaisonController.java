package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.SaisonDto;
import bookingengine.domain.entities.Saison;
import bookingengine.usecase.saison.SaisonUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/saisons")
@Tag(name = "Saisons", description = "API de gestion des saisons tarifaires pour le calcul dynamique des prix")
public class SaisonController {

    private final SaisonUseCase saisonUseCase;

    public SaisonController(SaisonUseCase saisonUseCase) {
        this.saisonUseCase = saisonUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Lister toutes les saisons",
            description = "Récupère la liste complète des saisons tarifaires définies dans le système"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des saisons récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<List<SaisonDto>> getAllSaisons() {
        List<SaisonDto> saisons = saisonUseCase.obtenirToutesSaisons()
                .stream()
                .map(SaisonDto::from)
                .toList();
        return ResponseEntity.ok(saisons);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Obtenir une saison par ID",
            description = "Récupère les détails d'une saison tarifaire spécifique par son identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saison trouvée",
                    content = @Content(schema = @Schema(implementation = SaisonDto.class))),
            @ApiResponse(responseCode = "404", description = "Saison non trouvée", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<SaisonDto> getSaisonById(@PathVariable Long id) {
        Saison saison = saisonUseCase.obtenirSaison(id);
        return ResponseEntity.ok(SaisonDto.from(saison));
    }

    @PostMapping
    @Operation(
            summary = "Créer une nouvelle saison",
            description = "Crée une nouvelle saison tarifaire avec son nom, ses dates de début/fin et son coefficient de prix"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Saison créée avec succès",
                    content = @Content(schema = @Schema(implementation = SaisonDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de saison invalides", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<SaisonDto> createSaison(@RequestBody SaisonDto saisonDto) {
        Saison saison = saisonUseCase.creerSaison(saisonDto.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(SaisonDto.from(saison));
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Modifier une saison existante",
            description = "Met à jour les informations d'une saison tarifaire existante"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saison modifiée avec succès",
                    content = @Content(schema = @Schema(implementation = SaisonDto.class))),
            @ApiResponse(responseCode = "404", description = "Saison non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données de saison invalides", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<SaisonDto> updateSaison(@PathVariable Long id, @RequestBody SaisonDto saisonDto) {
        Saison saison = saisonUseCase.modifierSaison(id, saisonDto.toDomain());
        return ResponseEntity.ok(SaisonDto.from(saison));
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Supprimer une saison",
            description = "Supprime définitivement une saison tarifaire du système"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Saison supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Saison non trouvée", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<Void> deleteSaison(@PathVariable Long id) {
        saisonUseCase.supprimerSaison(id);
        return ResponseEntity.noContent().build();
    }
}
