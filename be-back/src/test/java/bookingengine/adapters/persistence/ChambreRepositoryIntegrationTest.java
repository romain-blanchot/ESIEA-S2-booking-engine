package bookingengine.adapters.persistence;

import bookingengine.TestcontainersConfiguration;
import bookingengine.adapters.persistence.repositories.ChambreJpaRepository;
import bookingengine.domain.entities.Chambre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@DisplayName("ChambreRepository Integration Tests")
class ChambreRepositoryIntegrationTest {

    @Autowired
    private ChambreJpaRepository chambreJpaRepository;

    @Autowired
    private ChambreRepositoryImpl chambreRepository;

    @BeforeEach
    void setUp() {
        chambreJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("save Tests")
    class SaveTests {

        @Test
        @DisplayName("Should save new chambre")
        void shouldSaveNewChambre() {
            Chambre chambre = new Chambre(null, "101", "Double", 89.99, 2, "Description", true);

            Chambre saved = chambreRepository.save(chambre);

            assertNotNull(saved.getId());
            assertEquals("101", saved.getNumero());
            assertEquals("Double", saved.getType());
        }

        @Test
        @DisplayName("Should update existing chambre")
        void shouldUpdateExistingChambre() {
            Chambre chambre = new Chambre(null, "101", "Double", 89.99, 2, "Original", true);
            Chambre saved = chambreRepository.save(chambre);

            saved.setType("Suite");
            saved.setPrixBase(150.0);
            Chambre updated = chambreRepository.save(saved);

            assertEquals(saved.getId(), updated.getId());
            assertEquals("Suite", updated.getType());
            assertEquals(150.0, updated.getPrixBase());
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find chambre by id")
        void shouldFindChambreById() {
            Chambre chambre = new Chambre(null, "101", "Double", 89.99, 2, "Description", true);
            Chambre saved = chambreRepository.save(chambre);

            Optional<Chambre> found = chambreRepository.findById(saved.getId());

            assertTrue(found.isPresent());
            assertEquals(saved.getId(), found.get().getId());
        }

        @Test
        @DisplayName("Should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            Optional<Chambre> found = chambreRepository.findById(999L);
            assertTrue(found.isEmpty());
        }
    }

    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all chambres")
        void shouldReturnAllChambres() {
            chambreRepository.save(new Chambre(null, "101", "Double", 89.99, 2, "Desc 1", true));
            chambreRepository.save(new Chambre(null, "102", "Simple", 59.99, 1, "Desc 2", true));

            List<Chambre> all = chambreRepository.findAll();

            assertEquals(2, all.size());
        }
    }

    @Nested
    @DisplayName("findByDisponible Tests")
    class FindByDisponibleTests {

        @Test
        @DisplayName("Should find available chambres")
        void shouldFindAvailableChambres() {
            chambreRepository.save(new Chambre(null, "101", "Double", 89.99, 2, "Desc", true));
            chambreRepository.save(new Chambre(null, "102", "Simple", 59.99, 1, "Desc", false));

            List<Chambre> disponibles = chambreRepository.findByDisponible(true);

            assertEquals(1, disponibles.size());
            assertTrue(disponibles.get(0).isDisponible());
        }
    }

    @Nested
    @DisplayName("findByType Tests")
    class FindByTypeTests {

        @Test
        @DisplayName("Should find chambres by type")
        void shouldFindChambresByType() {
            chambreRepository.save(new Chambre(null, "101", "Double", 89.99, 2, "Desc", true));
            chambreRepository.save(new Chambre(null, "201", "Double", 99.99, 2, "Desc", true));
            chambreRepository.save(new Chambre(null, "301", "Suite", 199.99, 4, "Desc", true));

            List<Chambre> doubles = chambreRepository.findByType("Double");

            assertEquals(2, doubles.size());
        }
    }

    @Nested
    @DisplayName("deleteById Tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete chambre")
        void shouldDeleteChambre() {
            Chambre saved = chambreRepository.save(new Chambre(null, "101", "Double", 89.99, 2, "Desc", true));
            Long id = saved.getId();

            chambreRepository.deleteById(id);

            assertTrue(chambreRepository.findById(id).isEmpty());
        }
    }
}
