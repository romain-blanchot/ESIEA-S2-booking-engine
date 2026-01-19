package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.InscriptionRequest;
import bookingengine.domain.entities.Utilisateur;
import bookingengine.usecase.auth.AuthUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Mock
    private AuthUseCase authUseCase;

    @InjectMocks
    private AuthController authController;

    @Nested
    @DisplayName("POST /api/auth/inscription")
    class InscriptionTests {

        @Test
        @DisplayName("Should register user and return 201")
        void shouldRegisterUserAndReturn201() {
            Utilisateur savedUser = new Utilisateur();
            savedUser.setId(1L);
            savedUser.setUsername("jean.dupont");
            savedUser.setEmail("jean.dupont@email.com");
            savedUser.setRole("USER");

            when(authUseCase.inscrire("jean.dupont", "Password123!", "jean.dupont@email.com"))
                    .thenReturn(savedUser);

            InscriptionRequest request = new InscriptionRequest("jean.dupont", "Password123!", "jean.dupont@email.com");
            var response = authController.inscrire(request);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals("Utilisateur créé avec succès", response.getBody().get("message"));
            assertEquals("jean.dupont", response.getBody().get("username"));
        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            when(authUseCase.inscrire("existing", "Password123!", "new@email.com"))
                    .thenThrow(new IllegalArgumentException("Le nom d'utilisateur existe déjà"));

            InscriptionRequest request = new InscriptionRequest("existing", "Password123!", "new@email.com");

            assertThrows(IllegalArgumentException.class, () -> authController.inscrire(request));
        }
    }
}
