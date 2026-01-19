package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.SaisonDto;
import bookingengine.domain.entities.Saison;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.usecase.saison.SaisonUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaisonController Tests")
class SaisonControllerTest {

    @Mock
    private SaisonUseCase saisonUseCase;

    @InjectMocks
    private SaisonController saisonController;

    @Nested
    @DisplayName("GET /api/saisons")
    class GetAllSaisonsTests {

        @Test
        @DisplayName("Should return list of saisons")
        void shouldReturnListOfSaisons() {
            List<Saison> saisons = Arrays.asList(
                    new Saison(1L, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5),
                    new Saison(2L, "Basse Saison", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31), 0.8)
            );
            when(saisonUseCase.obtenirToutesSaisons()).thenReturn(saisons);

            var response = saisonController.getAllSaisons();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }
    }

    @Nested
    @DisplayName("GET /api/saisons/{id}")
    class GetSaisonByIdTests {

        @Test
        @DisplayName("Should return saison when found")
        void shouldReturnSaisonWhenFound() {
            Saison saison = new Saison(1L, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5);
            when(saisonUseCase.obtenirSaison(1L)).thenReturn(saison);

            var response = saisonController.getSaisonById(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Haute Saison", response.getBody().nom());
        }

        @Test
        @DisplayName("Should throw exception when saison not found")
        void shouldThrowExceptionWhenSaisonNotFound() {
            when(saisonUseCase.obtenirSaison(999L))
                    .thenThrow(new EntityNotFoundException("Saison non trouvée"));

            assertThrows(EntityNotFoundException.class, () -> saisonController.getSaisonById(999L));
        }
    }

    @Nested
    @DisplayName("POST /api/saisons")
    class CreateSaisonTests {

        @Test
        @DisplayName("Should create saison and return 201")
        void shouldCreateSaisonAndReturn201() {
            Saison savedSaison = new Saison(1L, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5);
            when(saisonUseCase.creerSaison(any(Saison.class))).thenReturn(savedSaison);

            SaisonDto dto = new SaisonDto(null, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5);
            var response = saisonController.createSaison(dto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(1L, response.getBody().id());
        }
    }

    @Nested
    @DisplayName("PUT /api/saisons/{id}")
    class UpdateSaisonTests {

        @Test
        @DisplayName("Should update saison and return 200")
        void shouldUpdateSaisonAndReturn200() {
            Saison updatedSaison = new Saison(1L, "Très Haute", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 2.0);
            when(saisonUseCase.modifierSaison(eq(1L), any(Saison.class))).thenReturn(updatedSaison);

            SaisonDto dto = new SaisonDto(1L, "Très Haute", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 2.0);
            var response = saisonController.updateSaison(1L, dto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2.0, response.getBody().coefficientPrix());
        }
    }

    @Nested
    @DisplayName("DELETE /api/saisons/{id}")
    class DeleteSaisonTests {

        @Test
        @DisplayName("Should delete saison and return 204")
        void shouldDeleteSaisonAndReturn204() {
            doNothing().when(saisonUseCase).supprimerSaison(1L);

            var response = saisonController.deleteSaison(1L);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(saisonUseCase).supprimerSaison(1L);
        }
    }
}
