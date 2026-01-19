package bookingengine.usecase.auth;

import bookingengine.domain.entities.Utilisateur;
import bookingengine.domain.ports.PasswordEncoderPort;
import bookingengine.domain.repositories.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthUseCase Tests")
class AuthUseCaseTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    private AuthUseCase authUseCase;

    @BeforeEach
    void setUp() {
        authUseCase = new AuthUseCase(utilisateurRepository, passwordEncoder);
    }

    @Nested
    @DisplayName("inscrire Tests")
    class InscrireTests {

        @Test
        @DisplayName("Should register user successfully")
        void shouldRegisterUserSuccessfully() {
            String username = "jean.dupont";
            String password = "password123";
            String email = "jean.dupont@email.com";
            String encodedPassword = "$2a$10$encodedPassword";

            when(utilisateurRepository.existsByUsername(username)).thenReturn(false);
            when(utilisateurRepository.existsByEmail(email)).thenReturn(false);
            when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

            Utilisateur savedUser = new Utilisateur();
            savedUser.setId(1L);
            savedUser.setUsername(username);
            savedUser.setPassword(encodedPassword);
            savedUser.setEmail(email);
            savedUser.setRole("USER");

            when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(savedUser);

            Utilisateur result = authUseCase.inscrire(username, password, email);

            assertEquals(1L, result.getId());
            assertEquals(username, result.getUsername());
            assertEquals(encodedPassword, result.getPassword());
            assertEquals(email, result.getEmail());
            assertEquals("USER", result.getRole());

            // Vérifier que le mot de passe a été encodé
            verify(passwordEncoder).encode(password);

            // Vérifier que l'utilisateur sauvegardé a le bon rôle
            ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);
            verify(utilisateurRepository).save(captor.capture());
            assertEquals("USER", captor.getValue().getRole());
        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            String username = "existing.user";
            String password = "password123";
            String email = "new@email.com";

            when(utilisateurRepository.existsByUsername(username)).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authUseCase.inscrire(username, password, email)
            );

            assertTrue(exception.getMessage().contains("nom d'utilisateur"));
            verify(utilisateurRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            String username = "new.user";
            String password = "password123";
            String email = "existing@email.com";

            when(utilisateurRepository.existsByUsername(username)).thenReturn(false);
            when(utilisateurRepository.existsByEmail(email)).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authUseCase.inscrire(username, password, email)
            );

            assertTrue(exception.getMessage().contains("email"));
            verify(utilisateurRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("verifierMotDePasse Tests")
    class VerifierMotDePasseTests {

        @Test
        @DisplayName("Should return true for valid credentials")
        void shouldReturnTrueForValidCredentials() {
            String username = "jean.dupont";
            String password = "password123";
            String encodedPassword = "$2a$10$encodedPassword";

            Utilisateur user = new Utilisateur();
            user.setUsername(username);
            user.setPassword(encodedPassword);

            when(utilisateurRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

            boolean result = authUseCase.verifierMotDePasse(username, password);

            assertTrue(result);
            verify(passwordEncoder).matches(password, encodedPassword);
        }

        @Test
        @DisplayName("Should return false for invalid password")
        void shouldReturnFalseForInvalidPassword() {
            String username = "jean.dupont";
            String password = "wrongPassword";
            String encodedPassword = "$2a$10$encodedPassword";

            Utilisateur user = new Utilisateur();
            user.setUsername(username);
            user.setPassword(encodedPassword);

            when(utilisateurRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

            boolean result = authUseCase.verifierMotDePasse(username, password);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when user not found")
        void shouldReturnFalseWhenUserNotFound() {
            String username = "unknown.user";
            String password = "password123";

            when(utilisateurRepository.findByUsername(username)).thenReturn(Optional.empty());

            boolean result = authUseCase.verifierMotDePasse(username, password);

            assertFalse(result);
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
    }
}
