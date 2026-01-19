package bookingengine.usecase.chambre;

import bookingengine.domain.entities.Chambre;
import bookingengine.domain.events.ChambreCreatedEvent;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.ports.EventPublisherPort;
import bookingengine.domain.repositories.ChambreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChambreUseCase Tests")
class ChambreUseCaseTest {

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    private ChambreUseCase chambreUseCase;

    @BeforeEach
    void setUp() {
        chambreUseCase = new ChambreUseCase(chambreRepository, eventPublisher);
    }

    @Nested
    @DisplayName("creerChambre Tests")
    class CreerChambreTests {

        @Test
        @DisplayName("Should create chambre and publish event")
        void shouldCreateChambreAndPublishEvent() {
            Chambre chambreToCreate = new Chambre(null, "101", "Double", 89.99, 2, "Description", true);
            Chambre savedChambre = new Chambre(1L, "101", "Double", 89.99, 2, "Description", true);

            when(chambreRepository.save(any(Chambre.class))).thenReturn(savedChambre);

            Chambre result = chambreUseCase.creerChambre(chambreToCreate);

            assertEquals(1L, result.getId());
            assertEquals("101", result.getNumero());
            verify(chambreRepository).save(chambreToCreate);

            ArgumentCaptor<ChambreCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ChambreCreatedEvent.class);
            verify(eventPublisher).publish(eventCaptor.capture());
            ChambreCreatedEvent publishedEvent = eventCaptor.getValue();
            assertEquals(1L, publishedEvent.chambreId());
            assertEquals("101", publishedEvent.numero());
            assertEquals("Double", publishedEvent.type());
            assertEquals(89.99, publishedEvent.prixBase());
        }
    }

    @Nested
    @DisplayName("modifierChambre Tests")
    class ModifierChambreTests {

        @Test
        @DisplayName("Should update chambre when exists")
        void shouldUpdateChambreWhenExists() {
            Long id = 1L;
            Chambre existingChambre = new Chambre(id, "101", "Double", 89.99, 2, "Old description", true);
            Chambre updatedChambre = new Chambre(null, "101", "Suite", 150.00, 2, "New description", true);
            Chambre savedChambre = new Chambre(id, "101", "Suite", 150.00, 2, "New description", true);

            when(chambreRepository.findById(id)).thenReturn(Optional.of(existingChambre));
            when(chambreRepository.save(any(Chambre.class))).thenReturn(savedChambre);

            Chambre result = chambreUseCase.modifierChambre(id, updatedChambre);

            assertEquals(id, result.getId());
            assertEquals("Suite", result.getType());
            assertEquals(150.00, result.getPrixBase());
            verify(chambreRepository).save(argThat(c -> c.getId().equals(id)));
        }

        @Test
        @DisplayName("Should throw exception when chambre not found")
        void shouldThrowExceptionWhenChambreNotFound() {
            Long id = 999L;
            Chambre chambre = new Chambre();

            when(chambreRepository.findById(id)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> chambreUseCase.modifierChambre(id, chambre)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(chambreRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("supprimerChambre Tests")
    class SupprimerChambreTests {

        @Test
        @DisplayName("Should delete chambre when exists")
        void shouldDeleteChambreWhenExists() {
            Long id = 1L;
            Chambre chambre = new Chambre(id, "101", "Double", 89.99, 2, "Description", true);

            when(chambreRepository.findById(id)).thenReturn(Optional.of(chambre));
            doNothing().when(chambreRepository).deleteById(id);

            chambreUseCase.supprimerChambre(id);

            verify(chambreRepository).deleteById(id);
        }

        @Test
        @DisplayName("Should throw exception when chambre not found")
        void shouldThrowExceptionWhenChambreNotFound() {
            Long id = 999L;

            when(chambreRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> chambreUseCase.supprimerChambre(id));
            verify(chambreRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("obtenirChambre Tests")
    class ObtenirChambreTests {

        @Test
        @DisplayName("Should return chambre when exists")
        void shouldReturnChambreWhenExists() {
            Long id = 1L;
            Chambre chambre = new Chambre(id, "101", "Double", 89.99, 2, "Description", true);

            when(chambreRepository.findById(id)).thenReturn(Optional.of(chambre));

            Chambre result = chambreUseCase.obtenirChambre(id);

            assertEquals(id, result.getId());
            assertEquals("101", result.getNumero());
        }

        @Test
        @DisplayName("Should throw exception when chambre not found")
        void shouldThrowExceptionWhenChambreNotFound() {
            Long id = 999L;

            when(chambreRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> chambreUseCase.obtenirChambre(id));
        }
    }

    @Nested
    @DisplayName("obtenirToutesChambres Tests")
    class ObtenirToutesChambresTests {

        @Test
        @DisplayName("Should return all chambres")
        void shouldReturnAllChambres() {
            List<Chambre> chambres = Arrays.asList(
                    new Chambre(1L, "101", "Double", 89.99, 2, "Desc 1", true),
                    new Chambre(2L, "102", "Simple", 59.99, 1, "Desc 2", true)
            );

            when(chambreRepository.findAll()).thenReturn(chambres);

            List<Chambre> result = chambreUseCase.obtenirToutesChambres();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no chambres")
        void shouldReturnEmptyListWhenNoChambres() {
            when(chambreRepository.findAll()).thenReturn(List.of());

            List<Chambre> result = chambreUseCase.obtenirToutesChambres();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenirChambresDisponibles Tests")
    class ObtenirChambresDisponiblesTests {

        @Test
        @DisplayName("Should return only available chambres")
        void shouldReturnOnlyAvailableChambres() {
            List<Chambre> chambresDisponibles = List.of(
                    new Chambre(1L, "101", "Double", 89.99, 2, "Desc", true)
            );

            when(chambreRepository.findByDisponible(true)).thenReturn(chambresDisponibles);

            List<Chambre> result = chambreUseCase.obtenirChambresDisponibles();

            assertEquals(1, result.size());
            assertTrue(result.get(0).isDisponible());
        }
    }

    @Nested
    @DisplayName("obtenirChambresParType Tests")
    class ObtenirChambresParTypeTests {

        @Test
        @DisplayName("Should return chambres filtered by type")
        void shouldReturnChambresFilteredByType() {
            String type = "Suite";
            List<Chambre> suites = List.of(
                    new Chambre(3L, "301", "Suite", 199.99, 3, "Suite luxe", true)
            );

            when(chambreRepository.findByType(type)).thenReturn(suites);

            List<Chambre> result = chambreUseCase.obtenirChambresParType(type);

            assertEquals(1, result.size());
            assertEquals("Suite", result.get(0).getType());
        }
    }
}
