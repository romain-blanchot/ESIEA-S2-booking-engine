package bookingengine.adapters.persistence;

import bookingengine.TestcontainersConfiguration;
import bookingengine.adapters.persistence.repositories.SaisonJpaRepository;
import bookingengine.domain.entities.Saison;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@DisplayName("SaisonRepository Integration Tests")
class SaisonRepositoryIntegrationTest {

    @Autowired
    private SaisonJpaRepository saisonJpaRepository;

    @Autowired
    private SaisonRepositoryImpl saisonRepository;

    @BeforeEach
    void setUp() {
        saisonJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("save Tests")
    class SaveTests {

        @Test
        @DisplayName("Should save new saison")
        void shouldSaveNewSaison() {
            Saison saison = new Saison(null, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5);

            Saison saved = saisonRepository.save(saison);

            assertNotNull(saved.getId());
            assertEquals("Haute Saison", saved.getNom());
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find saison by id")
        void shouldFindSaisonById() {
            Saison saved = saisonRepository.save(new Saison(null, "Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5));

            Optional<Saison> found = saisonRepository.findById(saved.getId());

            assertTrue(found.isPresent());
            assertEquals("Saison", found.get().getNom());
        }
    }

    @Nested
    @DisplayName("findByDate Tests")
    class FindByDateTests {

        @Test
        @DisplayName("Should find saison for date within range")
        void shouldFindSaisonForDateWithinRange() {
            saisonRepository.save(new Saison(null, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5));

            Optional<Saison> found = saisonRepository.findByDate(LocalDate.of(2024, 7, 15));

            assertTrue(found.isPresent());
            assertEquals("Haute Saison", found.get().getNom());
        }

        @Test
        @DisplayName("Should return empty for date outside range")
        void shouldReturnEmptyForDateOutsideRange() {
            saisonRepository.save(new Saison(null, "Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5));

            Optional<Saison> found = saisonRepository.findByDate(LocalDate.of(2024, 5, 15));

            assertTrue(found.isEmpty());
        }
    }

    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all saisons")
        void shouldReturnAllSaisons() {
            saisonRepository.save(new Saison(null, "Haute", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5));
            saisonRepository.save(new Saison(null, "Basse", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31), 0.8));

            List<Saison> all = saisonRepository.findAll();

            assertEquals(2, all.size());
        }
    }
}
