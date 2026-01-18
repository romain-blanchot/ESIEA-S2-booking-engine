package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.ChambreDto;
import bookingengine.domain.entities.Chambre;
import bookingengine.usecase.chambre.ChambreUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chambres")
@Tag(name = "Chambres", description = "Gestion des chambres")
public class ChambreController {

    private final ChambreUseCase chambreUseCase;

    public ChambreController(ChambreUseCase chambreUseCase) {
        this.chambreUseCase = chambreUseCase;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les chambres")
    public ResponseEntity<List<ChambreDto>> getAllChambres() {
        List<ChambreDto> chambres = chambreUseCase.obtenirToutesChambres()
                .stream()
                .map(ChambreDto::from)
                .toList();
        return ResponseEntity.ok(chambres);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une chambre par ID")
    public ResponseEntity<ChambreDto> getChambreById(@PathVariable Long id) {
        Chambre chambre = chambreUseCase.obtenirChambre(id);
        return ResponseEntity.ok(ChambreDto.from(chambre));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Lister les chambres disponibles")
    public ResponseEntity<List<ChambreDto>> getChambresDisponibles() {
        List<ChambreDto> chambres = chambreUseCase.obtenirChambresDisponibles()
                .stream()
                .map(ChambreDto::from)
                .toList();
        return ResponseEntity.ok(chambres);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Lister les chambres par type")
    public ResponseEntity<List<ChambreDto>> getChambresByType(@PathVariable String type) {
        List<ChambreDto> chambres = chambreUseCase.obtenirChambresParType(type)
                .stream()
                .map(ChambreDto::from)
                .toList();
        return ResponseEntity.ok(chambres);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle chambre")
    public ResponseEntity<ChambreDto> createChambre(@RequestBody ChambreDto chambreDto) {
        Chambre chambre = chambreUseCase.creerChambre(chambreDto.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(ChambreDto.from(chambre));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une chambre existante")
    public ResponseEntity<ChambreDto> updateChambre(@PathVariable Long id, @RequestBody ChambreDto chambreDto) {
        Chambre chambre = chambreUseCase.modifierChambre(id, chambreDto.toDomain());
        return ResponseEntity.ok(ChambreDto.from(chambre));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une chambre")
    public ResponseEntity<Void> deleteChambre(@PathVariable Long id) {
        chambreUseCase.supprimerChambre(id);
        return ResponseEntity.noContent().build();
    }
}
