package bookingengine.usecase.prix;

import bookingengine.domain.entities.Chambre;
import bookingengine.domain.entities.Saison;
import bookingengine.domain.events.PrixCalculatedEvent;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.ports.EventPublisherPort;
import bookingengine.domain.repositories.ChambreRepository;
import bookingengine.domain.repositories.SaisonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalculPrixUseCase Tests")
class CalculPrixUseCaseTest {

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private SaisonRepository saisonRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    private CalculPrixUseCase calculPrixUseCase;

    @BeforeEach
    void setUp() {
        calculPrixUseCase = new CalculPrixUseCase(chambreRepository, saisonRepository, eventPublisher);
    }

    @Nested
    @DisplayName("calculerPrix Tests")
    class CalculerPrixTests {

        @Test
        @DisplayName("Should calculate price without season (base price)")
        void shouldCalculatePriceWithoutSeason() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2024, 5, 1);
            LocalDate fin = LocalDate.of(2024, 5, 4); // 3 nights
            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));
            when(saisonRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());

            double prix = calculPrixUseCase.calculerPrix(chambreId, debut, fin);

            assertEquals(300.0, prix); // 3 nights * 100€
        }

        @Test
        @DisplayName("Should calculate price with season coefficient")
        void shouldCalculatePriceWithSeasonCoefficient() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2024, 7, 15);
            LocalDate fin = LocalDate.of(2024, 7, 17); // 2 nights
            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);
            Saison hauteSaison = new Saison(1L, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));
            when(saisonRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.of(hauteSaison));

            double prix = calculPrixUseCase.calculerPrix(chambreId, debut, fin);

            assertEquals(300.0, prix); // 2 nights * 100€ * 1.5
        }

        @Test
        @DisplayName("Should throw exception when chambre not found")
        void shouldThrowExceptionWhenChambreNotFound() {
            Long chambreId = 999L;
            LocalDate debut = LocalDate.of(2024, 7, 15);
            LocalDate fin = LocalDate.of(2024, 7, 17);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> calculPrixUseCase.calculerPrix(chambreId, debut, fin));
        }

        @Test
        @DisplayName("Should throw exception when end date before start date")
        void shouldThrowExceptionWhenEndDateBeforeStartDate() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2024, 7, 20);
            LocalDate fin = LocalDate.of(2024, 7, 15);
            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> calculPrixUseCase.calculerPrix(chambreId, debut, fin));

            assertTrue(exception.getMessage().contains("date de fin"));
        }

        @Test
        @DisplayName("Should throw exception when same start and end date")
        void shouldThrowExceptionWhenSameStartAndEndDate() {
            Long chambreId = 1L;
            LocalDate date = LocalDate.of(2024, 7, 15);
            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));

            assertThrows(IllegalArgumentException.class,
                    () -> calculPrixUseCase.calculerPrix(chambreId, date, date));
        }

        @Test
        @DisplayName("Should round price to 2 decimal places")
        void shouldRoundPriceToTwoDecimalPlaces() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2024, 7, 15);
            LocalDate fin = LocalDate.of(2024, 7, 18); // 3 nights
            Chambre chambre = new Chambre(chambreId, "101", "Double", 33.33, 2, "Desc", true);
            Saison saison = new Saison(1L, "Saison", LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 31), 1.333);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));
            when(saisonRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.of(saison));

            double prix = calculPrixUseCase.calculerPrix(chambreId, debut, fin);

            // Vérifier que le prix est arrondi à 2 décimales
            assertEquals(prix, Math.round(prix * 100.0) / 100.0);
        }
    }

    @Nested
    @DisplayName("calculerPrixDetaille Tests")
    class CalculerPrixDetailleTests {

        @Test
        @DisplayName("Should return detailed calculation with day breakdown")
        void shouldReturnDetailedCalculationWithDayBreakdown() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2024, 7, 15);
            LocalDate fin = LocalDate.of(2024, 7, 18); // 3 nights
            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);
            Saison hauteSaison = new Saison(1L, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));
            when(saisonRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.of(hauteSaison));

            CalculPrixUseCase.ResultatCalculPrix result = calculPrixUseCase.calculerPrixDetaille(chambreId, debut, fin);

            assertEquals("101", result.numeroChambre());
            assertEquals("Double", result.typeChambre());
            assertEquals(debut, result.dateDebut());
            assertEquals(fin, result.dateFin());
            assertEquals(3, result.nombreNuits());
            assertEquals(100.0, result.prixBaseParNuit());
            assertEquals(450.0, result.prixTotal()); // 3 * 100 * 1.5
            assertEquals(3, result.detailsParJour().size());
        }

        @Test
        @DisplayName("Should calculate mixed seasons correctly")
        void shouldCalculateMixedSeasonsCorrectly() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2024, 5, 30); // 2 days without season
            LocalDate fin = LocalDate.of(2024, 6, 2); // 2 days with season = 4 nights total? Actually 3 nights
            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);
            Saison hauteSaison = new Saison(1L, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));
            when(saisonRepository.findByDate(LocalDate.of(2024, 5, 30))).thenReturn(Optional.empty());
            when(saisonRepository.findByDate(LocalDate.of(2024, 5, 31))).thenReturn(Optional.empty());
            when(saisonRepository.findByDate(LocalDate.of(2024, 6, 1))).thenReturn(Optional.of(hauteSaison));

            CalculPrixUseCase.ResultatCalculPrix result = calculPrixUseCase.calculerPrixDetaille(chambreId, debut, fin);

            assertEquals(3, result.nombreNuits());
            assertEquals(3, result.detailsParJour().size());

            // Vérifier les détails par jour
            assertEquals("Hors saison", result.detailsParJour().get(0).saison());
            assertEquals(1.0, result.detailsParJour().get(0).coefficient());
            assertEquals(100.0, result.detailsParJour().get(0).prix());

            assertEquals("Haute Saison", result.detailsParJour().get(2).saison());
            assertEquals(1.5, result.detailsParJour().get(2).coefficient());
            assertEquals(150.0, result.detailsParJour().get(2).prix());
        }

        @Test
        @DisplayName("Should publish event after detailed calculation")
        void shouldPublishEventAfterDetailedCalculation() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2024, 7, 15);
            LocalDate fin = LocalDate.of(2024, 7, 17);
            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));
            when(saisonRepository.findByDate(any(LocalDate.class))).thenReturn(Optional.empty());

            calculPrixUseCase.calculerPrixDetaille(chambreId, debut, fin);

            ArgumentCaptor<PrixCalculatedEvent> eventCaptor = ArgumentCaptor.forClass(PrixCalculatedEvent.class);
            verify(eventPublisher).publish(eventCaptor.capture());
            PrixCalculatedEvent event = eventCaptor.getValue();

            assertEquals(chambreId, event.chambreId());
            assertEquals("101", event.numeroChambre());
            assertEquals("Double", event.typeChambre());
            assertEquals(2, event.nombreNuits());
            assertEquals(200.0, event.prixTotal());
        }

        @Test
        @DisplayName("Should throw exception for invalid dates in detailed calculation")
        void shouldThrowExceptionForInvalidDatesInDetailedCalculation() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2024, 7, 20);
            LocalDate fin = LocalDate.of(2024, 7, 15);
            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));

            assertThrows(IllegalArgumentException.class,
                    () -> calculPrixUseCase.calculerPrixDetaille(chambreId, debut, fin));
        }
    }

    @Nested
    @DisplayName("DetailJour Record Tests")
    class DetailJourRecordTests {

        @Test
        @DisplayName("Should create DetailJour record correctly")
        void shouldCreateDetailJourRecordCorrectly() {
            LocalDate date = LocalDate.of(2024, 7, 15);
            CalculPrixUseCase.DetailJour detailJour = new CalculPrixUseCase.DetailJour(date, "Haute Saison", 1.5, 150.0);

            assertEquals(date, detailJour.date());
            assertEquals("Haute Saison", detailJour.saison());
            assertEquals(1.5, detailJour.coefficient());
            assertEquals(150.0, detailJour.prix());
        }
    }
}
