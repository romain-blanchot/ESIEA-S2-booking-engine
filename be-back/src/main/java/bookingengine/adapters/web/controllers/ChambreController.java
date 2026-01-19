package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.ChambreDto;
import bookingengine.domain.entities.Chambre;
import bookingengine.usecase.chambre.ChambreUseCase;
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
@RequestMapping("api/chambres")
@Tag(name = "Chambres", description = "API de gestion des chambres d'hôtel")
public class ChambreController {

    private final ChambreUseCase chambreUseCase;

    public ChambreController(ChambreUseCase chambreUseCase) {
        this.chambreUseCase = chambreUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Lister toutes les chambres",
            description = "Récupère la liste complète des chambres disponibles dans l'hôtel"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des chambres récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<List<ChambreDto>> getAllChambres() {
        List<ChambreDto> chambres = chambreUseCase.obtenirToutesChambres()
                .stream()
                .map(ChambreDto::from)
                .toList();
        return ResponseEntity.ok(chambres);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Obtenir une chambre par ID",
            description = "Récupère les détails complets d'une chambre spécifique par son identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chambre trouvée",
                    content = @Content(schema = @Schema(implementation = ChambreDto.class))),
            @ApiResponse(responseCode = "404", description = "Chambre non trouvée", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<ChambreDto> getChambreById(@PathVariable Long id) {
        Chambre chambre = chambreUseCase.obtenirChambre(id);
        return ResponseEntity.ok(ChambreDto.from(chambre));
    }

    @GetMapping("disponibles")
    @Operation(
            summary = "Lister les chambres disponibles",
            description = "Récupère uniquement les chambres actuellement marquées comme disponibles"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des chambres disponibles récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<List<ChambreDto>> getChambresDisponibles() {
        List<ChambreDto> chambres = chambreUseCase.obtenirChambresDisponibles()
                .stream()
                .map(ChambreDto::from)
                .toList();
        return ResponseEntity.ok(chambres);
    }

    @GetMapping("type/{type}")
    @Operation(
            summary = "Lister les chambres par type",
            description = "Filtre les chambres par leur type (Simple, Double, Suite, etc.)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des chambres du type demandé récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<List<ChambreDto>> getChambresByType(@PathVariable String type) {
        List<ChambreDto> chambres = chambreUseCase.obtenirChambresParType(type)
                .stream()
                .map(ChambreDto::from)
                .toList();
        return ResponseEntity.ok(chambres);
    }

    @PostMapping
    @Operation(
            summary = "Créer une nouvelle chambre",
            description = "Ajoute une nouvelle chambre à l'inventaire de l'hôtel avec ses caractéristiques"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chambre créée avec succès",
                    content = @Content(schema = @Schema(implementation = ChambreDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de chambre invalides", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<ChambreDto> createChambre(@RequestBody ChambreDto chambreDto) {
        Chambre chambre = chambreUseCase.creerChambre(chambreDto.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(ChambreDto.from(chambre));
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Modifier une chambre existante",
            description = "Met à jour les informations d'une chambre existante"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chambre modifiée avec succès",
                    content = @Content(schema = @Schema(implementation = ChambreDto.class))),
            @ApiResponse(responseCode = "404", description = "Chambre non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données de chambre invalides", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<ChambreDto> updateChambre(@PathVariable Long id, @RequestBody ChambreDto chambreDto) {
        Chambre chambre = chambreUseCase.modifierChambre(id, chambreDto.toDomain());
        return ResponseEntity.ok(ChambreDto.from(chambre));
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Supprimer une chambre",
            description = "Supprime définitivement une chambre de l'inventaire"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Chambre supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Chambre non trouvée", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<Void> deleteChambre(@PathVariable Long id) {
        chambreUseCase.supprimerChambre(id);
        return ResponseEntity.noContent().build();
    }
}
