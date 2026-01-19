package bookingengine.usecase.saison;

import bookingengine.domain.entities.Saison;
import bookingengine.domain.events.SaisonCreatedEvent;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.ports.EventPublisherPort;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaisonUseCase Tests")
class SaisonUseCaseTest {

    @Mock
    private SaisonRepository saisonRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    private SaisonUseCase saisonUseCase;

    @BeforeEach
    void setUp() {
        saisonUseCase = new SaisonUseCase(saisonRepository, eventPublisher);
    }

    @Nested
    @DisplayName("creerSaison Tests")
    class CreerSaisonTests {

        @Test
        @DisplayName("Should create saison and publish event")
        void shouldCreateSaisonAndPublishEvent() {
            LocalDate debut = LocalDate.of(2024, 6, 1);
            LocalDate fin = LocalDate.of(2024, 8, 31);
            Saison saisonToCreate = new Saison(null, "Haute Saison", debut, fin, 1.5);
            Saison savedSaison = new Saison(1L, "Haute Saison", debut, fin, 1.5);

            when(saisonRepository.save(any(Saison.class))).thenReturn(savedSaison);

            Saison result = saisonUseCase.creerSaison(saisonToCreate);

            assertEquals(1L, result.getId());
            assertEquals("Haute Saison", result.getNom());
            verify(saisonRepository).save(saisonToCreate);

            ArgumentCaptor<SaisonCreatedEvent> eventCaptor = ArgumentCaptor.forClass(SaisonCreatedEvent.class);
            verify(eventPublisher).publish(eventCaptor.capture());
            SaisonCreatedEvent publishedEvent = eventCaptor.getValue();
            assertEquals(1L, publishedEvent.saisonId());
            assertEquals("Haute Saison", publishedEvent.nom());
            assertEquals(1.5, publishedEvent.coefficientPrix());
        }
    }

    @Nested
    @DisplayName("modifierSaison Tests")
    class ModifierSaisonTests {

        @Test
        @DisplayName("Should update saison when exists")
        void shouldUpdateSaisonWhenExists() {
            Long id = 1L;
            LocalDate debut = LocalDate.of(2024, 6, 1);
            LocalDate fin = LocalDate.of(2024, 8, 31);
            Saison existingSaison = new Saison(id, "Haute Saison", debut, fin, 1.5);
            Saison updatedSaison = new Saison(null, "Très Haute Saison", debut, fin, 2.0);
            Saison savedSaison = new Saison(id, "Très Haute Saison", debut, fin, 2.0);

            when(saisonRepository.findById(id)).thenReturn(Optional.of(existingSaison));
            when(saisonRepository.save(any(Saison.class))).thenReturn(savedSaison);

            Saison result = saisonUseCase.modifierSaison(id, updatedSaison);

            assertEquals(id, result.getId());
            assertEquals("Très Haute Saison", result.getNom());
            assertEquals(2.0, result.getCoefficientPrix());
        }

        @Test
        @DisplayName("Should throw exception when saison not found")
        void shouldThrowExceptionWhenSaisonNotFound() {
            Long id = 999L;
            Saison saison = new Saison();

            when(saisonRepository.findById(id)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> saisonUseCase.modifierSaison(id, saison)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(saisonRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("supprimerSaison Tests")
    class SupprimerSaisonTests {

        @Test
        @DisplayName("Should delete saison when exists")
        void shouldDeleteSaisonWhenExists() {
            Long id = 1L;
            LocalDate debut = LocalDate.of(2024, 6, 1);
            LocalDate fin = LocalDate.of(2024, 8, 31);
            Saison saison = new Saison(id, "Haute Saison", debut, fin, 1.5);

            when(saisonRepository.findById(id)).thenReturn(Optional.of(saison));
            doNothing().when(saisonRepository).deleteById(id);

            saisonUseCase.supprimerSaison(id);

            verify(saisonRepository).deleteById(id);
        }

        @Test
        @DisplayName("Should throw exception when saison not found")
        void shouldThrowExceptionWhenSaisonNotFound() {
            Long id = 999L;

            when(saisonRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> saisonUseCase.supprimerSaison(id));
            verify(saisonRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("obtenirSaison Tests")
    class ObtenirSaisonTests {

        @Test
        @DisplayName("Should return saison when exists")
        void shouldReturnSaisonWhenExists() {
            Long id = 1L;
            LocalDate debut = LocalDate.of(2024, 6, 1);
            LocalDate fin = LocalDate.of(2024, 8, 31);
            Saison saison = new Saison(id, "Haute Saison", debut, fin, 1.5);

            when(saisonRepository.findById(id)).thenReturn(Optional.of(saison));

            Saison result = saisonUseCase.obtenirSaison(id);

            assertEquals(id, result.getId());
            assertEquals("Haute Saison", result.getNom());
        }

        @Test
        @DisplayName("Should throw exception when saison not found")
        void shouldThrowExceptionWhenSaisonNotFound() {
            Long id = 999L;

            when(saisonRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> saisonUseCase.obtenirSaison(id));
        }
    }

    @Nested
    @DisplayName("obtenirToutesSaisons Tests")
    class ObtenirToutesSaisonsTests {

        @Test
        @DisplayName("Should return all saisons")
        void shouldReturnAllSaisons() {
            List<Saison> saisons = Arrays.asList(
                    new Saison(1L, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5),
                    new Saison(2L, "Basse Saison", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31), 0.8)
            );

            when(saisonRepository.findAll()).thenReturn(saisons);

            List<Saison> result = saisonUseCase.obtenirToutesSaisons();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no saisons")
        void shouldReturnEmptyListWhenNoSaisons() {
            when(saisonRepository.findAll()).thenReturn(List.of());

            List<Saison> result = saisonUseCase.obtenirToutesSaisons();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenirSaisonParDate Tests")
    class ObtenirSaisonParDateTests {

        @Test
        @DisplayName("Should return saison for date when exists")
        void shouldReturnSaisonForDateWhenExists() {
            LocalDate date = LocalDate.of(2024, 7, 15);
            Saison saison = new Saison(1L, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5);

            when(saisonRepository.findByDate(date)).thenReturn(Optional.of(saison));

            Optional<Saison> result = saisonUseCase.obtenirSaisonParDate(date);

            assertTrue(result.isPresent());
            assertEquals("Haute Saison", result.get().getNom());
        }

        @Test
        @DisplayName("Should return empty when no saison for date")
        void shouldReturnEmptyWhenNoSaisonForDate() {
            LocalDate date = LocalDate.of(2024, 5, 1);

            when(saisonRepository.findByDate(date)).thenReturn(Optional.empty());

            Optional<Saison> result = saisonUseCase.obtenirSaisonParDate(date);

            assertTrue(result.isEmpty());
        }
    }
}
