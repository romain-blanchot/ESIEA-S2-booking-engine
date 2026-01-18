package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.SaisonDto;
import bookingengine.domain.entities.Saison;
import bookingengine.usecase.saison.SaisonUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saisons")
@Tag(name = "Saisons", description = "Gestion des saisons tarifaires")
public class SaisonController {

    private final SaisonUseCase saisonUseCase;

    public SaisonController(SaisonUseCase saisonUseCase) {
        this.saisonUseCase = saisonUseCase;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les saisons")
    public ResponseEntity<List<SaisonDto>> getAllSaisons() {
        List<SaisonDto> saisons = saisonUseCase.obtenirToutesSaisons()
                .stream()
                .map(SaisonDto::from)
                .toList();
        return ResponseEntity.ok(saisons);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une saison par ID")
    public ResponseEntity<SaisonDto> getSaisonById(@PathVariable Long id) {
        Saison saison = saisonUseCase.obtenirSaison(id);
        return ResponseEntity.ok(SaisonDto.from(saison));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle saison")
    public ResponseEntity<SaisonDto> createSaison(@RequestBody SaisonDto saisonDto) {
        Saison saison = saisonUseCase.creerSaison(saisonDto.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(SaisonDto.from(saison));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une saison existante")
    public ResponseEntity<SaisonDto> updateSaison(@PathVariable Long id, @RequestBody SaisonDto saisonDto) {
        Saison saison = saisonUseCase.modifierSaison(id, saisonDto.toDomain());
        return ResponseEntity.ok(SaisonDto.from(saison));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une saison")
    public ResponseEntity<Void> deleteSaison(@PathVariable Long id) {
        saisonUseCase.supprimerSaison(id);
        return ResponseEntity.noContent().build();
    }
}
