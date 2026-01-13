package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.InscriptionRequest;
import bookingengine.domain.entities.Utilisateur;
import bookingengine.usecase.auth.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Gestion de l'authentification")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/inscription")
    @Operation(summary = "Inscrire un nouvel utilisateur")
    public ResponseEntity<Map<String, String>> inscrire(@RequestBody InscriptionRequest request) {
        Utilisateur utilisateur = authUseCase.inscrire(
                request.username(),
                request.password(),
                request.email()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Utilisateur créé avec succès",
                        "username", utilisateur.getUsername()
                ));
    }
}
