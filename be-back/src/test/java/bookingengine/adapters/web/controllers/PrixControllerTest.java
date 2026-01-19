package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.CalculPrixRequest;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.usecase.prix.CalculPrixUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PrixController Tests")
class PrixControllerTest {

    @Mock
    private CalculPrixUseCase calculPrixUseCase;

    @InjectMocks
    private PrixController prixController;

    @Nested
    @DisplayName("POST /api/prix/calculer")
    class CalculerPrixTests {

        @Test
        @DisplayName("Should calculate price and return detailed result")
        void shouldCalculatePriceAndReturnDetailedResult() {
            LocalDate debut = LocalDate.of(2024, 7, 15);
            LocalDate fin = LocalDate.of(2024, 7, 18);

            List<CalculPrixUseCase.DetailJour> details = List.of(
                    new CalculPrixUseCase.DetailJour(LocalDate.of(2024, 7, 15), "Haute Saison", 1.5, 150.0),
                    new CalculPrixUseCase.DetailJour(LocalDate.of(2024, 7, 16), "Haute Saison", 1.5, 150.0),
                    new CalculPrixUseCase.DetailJour(LocalDate.of(2024, 7, 17), "Haute Saison", 1.5, 150.0)
            );

            CalculPrixUseCase.ResultatCalculPrix resultat = new CalculPrixUseCase.ResultatCalculPrix(
                    "101", "Double", debut, fin, 3, 100.0, 1.5, 450.0, details
            );

            when(calculPrixUseCase.calculerPrixDetaille(1L, debut, fin)).thenReturn(resultat);

            CalculPrixRequest request = new CalculPrixRequest(1L, debut, fin);
            var response = prixController.calculerPrix(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("101", response.getBody().numeroChambre());
            assertEquals(3, response.getBody().nombreNuits());
            assertEquals(450.0, response.getBody().prixTotal());
        }

        @Test
        @DisplayName("Should throw exception when chambre not found")
        void shouldThrowExceptionWhenChambreNotFound() {
            LocalDate debut = LocalDate.of(2024, 7, 15);
            LocalDate fin = LocalDate.of(2024, 7, 18);

            when(calculPrixUseCase.calculerPrixDetaille(999L, debut, fin))
                    .thenThrow(new EntityNotFoundException("Chambre non trouvée"));

            CalculPrixRequest request = new CalculPrixRequest(999L, debut, fin);

            assertThrows(EntityNotFoundException.class, () -> prixController.calculerPrix(request));
        }

        @Test
        @DisplayName("Should throw exception when dates are invalid")
        void shouldThrowExceptionWhenDatesAreInvalid() {
            LocalDate debut = LocalDate.of(2024, 7, 20);
            LocalDate fin = LocalDate.of(2024, 7, 15);

            when(calculPrixUseCase.calculerPrixDetaille(1L, debut, fin))
                    .thenThrow(new IllegalArgumentException("La date de fin doit être après la date de début"));

            CalculPrixRequest request = new CalculPrixRequest(1L, debut, fin);

            assertThrows(IllegalArgumentException.class, () -> prixController.calculerPrix(request));
        }
    }
}
