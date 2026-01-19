package bookingengine.adapters.web.controllers;

import bookingengine.domain.entities.Chambre;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.usecase.chambre.ChambreUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChambreController Tests")
class ChambreControllerTest {

    @Mock
    private ChambreUseCase chambreUseCase;

    @InjectMocks
    private ChambreController chambreController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("GET /api/chambres")
    class GetAllChambresTests {

        @Test
        @DisplayName("Should return list of chambres")
        void shouldReturnListOfChambres() {
            List<Chambre> chambres = Arrays.asList(
                    new Chambre(1L, "101", "Double", 89.99, 2, "Desc 1", true),
                    new Chambre(2L, "102", "Simple", 59.99, 1, "Desc 2", true)
            );
            when(chambreUseCase.obtenirToutesChambres()).thenReturn(chambres);

            var response = chambreController.getAllChambres();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }

        @Test
        @DisplayName("Should return empty list when no chambres")
        void shouldReturnEmptyListWhenNoChambres() {
            when(chambreUseCase.obtenirToutesChambres()).thenReturn(List.of());

            var response = chambreController.getAllChambres();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/chambres/{id}")
    class GetChambreByIdTests {

        @Test
        @DisplayName("Should return chambre when found")
        void shouldReturnChambreWhenFound() {
            Chambre chambre = new Chambre(1L, "101", "Double", 89.99, 2, "Description", true);
            when(chambreUseCase.obtenirChambre(1L)).thenReturn(chambre);

            var response = chambreController.getChambreById(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("101", response.getBody().numero());
        }

        @Test
        @DisplayName("Should throw exception when chambre not found")
        void shouldThrowExceptionWhenChambreNotFound() {
            when(chambreUseCase.obtenirChambre(999L))
                    .thenThrow(new EntityNotFoundException("Chambre non trouvée"));

            assertThrows(EntityNotFoundException.class, () -> chambreController.getChambreById(999L));
        }
    }

    @Nested
    @DisplayName("GET /api/chambres/disponibles")
    class GetChambresDisponiblesTests {

        @Test
        @DisplayName("Should return only available chambres")
        void shouldReturnOnlyAvailableChambres() {
            List<Chambre> disponibles = List.of(
                    new Chambre(1L, "101", "Double", 89.99, 2, "Desc", true)
            );
            when(chambreUseCase.obtenirChambresDisponibles()).thenReturn(disponibles);

            var response = chambreController.getChambresDisponibles();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
            assertTrue(response.getBody().get(0).disponible());
        }
    }

    @Nested
    @DisplayName("GET /api/chambres/type/{type}")
    class GetChambresByTypeTests {

        @Test
        @DisplayName("Should return chambres filtered by type")
        void shouldReturnChambresFilteredByType() {
            List<Chambre> suites = List.of(
                    new Chambre(3L, "301", "Suite", 199.99, 3, "Suite luxe", true)
            );
            when(chambreUseCase.obtenirChambresParType("Suite")).thenReturn(suites);

            var response = chambreController.getChambresByType("Suite");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
            assertEquals("Suite", response.getBody().get(0).type());
        }
    }

    @Nested
    @DisplayName("POST /api/chambres")
    class CreateChambreTests {

        @Test
        @DisplayName("Should create chambre and return 201")
        void shouldCreateChambreAndReturn201() {
            Chambre savedChambre = new Chambre(1L, "101", "Double", 89.99, 2, "Description", true);
            when(chambreUseCase.creerChambre(any(Chambre.class))).thenReturn(savedChambre);

            var dto = new bookingengine.adapters.web.dto.ChambreDto(null, "101", "Double", 89.99, 2, "Description", true);
            var response = chambreController.createChambre(dto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(1L, response.getBody().id());
            assertEquals("101", response.getBody().numero());
        }
    }

    @Nested
    @DisplayName("PUT /api/chambres/{id}")
    class UpdateChambreTests {

        @Test
        @DisplayName("Should update chambre and return 200")
        void shouldUpdateChambreAndReturn200() {
            Chambre updatedChambre = new Chambre(1L, "101", "Suite", 150.0, 2, "Updated", true);
            when(chambreUseCase.modifierChambre(eq(1L), any(Chambre.class))).thenReturn(updatedChambre);

            var dto = new bookingengine.adapters.web.dto.ChambreDto(1L, "101", "Suite", 150.0, 2, "Updated", true);
            var response = chambreController.updateChambre(1L, dto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Suite", response.getBody().type());
        }
    }

    @Nested
    @DisplayName("DELETE /api/chambres/{id}")
    class DeleteChambreTests {

        @Test
        @DisplayName("Should delete chambre and return 204")
        void shouldDeleteChambreAndReturn204() {
            doNothing().when(chambreUseCase).supprimerChambre(1L);

            var response = chambreController.deleteChambre(1L);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(chambreUseCase).supprimerChambre(1L);
        }
    }
}
