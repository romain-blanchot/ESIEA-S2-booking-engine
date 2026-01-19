package bookingengine.adapters.persistence.mappers;

import bookingengine.adapters.persistence.entities.SaisonJpaEntity;
import bookingengine.domain.entities.Saison;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SaisonMapper Tests")
class SaisonMapperTest {

    private SaisonMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SaisonMapper();
    }

    @Nested
    @DisplayName("toDomain Tests")
    class ToDomainTests {

        @Test
        @DisplayName("Should convert entity to domain")
        void shouldConvertEntityToDomain() {
            SaisonJpaEntity entity = new SaisonJpaEntity();
            entity.setId(1L);
            entity.setNom("Haute Saison");
            entity.setDateDebut(LocalDate.of(2024, 6, 1));
            entity.setDateFin(LocalDate.of(2024, 8, 31));
            entity.setCoefficientPrix(1.5);

            Saison domain = mapper.toDomain(entity);

            assertEquals(1L, domain.getId());
            assertEquals("Haute Saison", domain.getNom());
            assertEquals(LocalDate.of(2024, 6, 1), domain.getDateDebut());
            assertEquals(LocalDate.of(2024, 8, 31), domain.getDateFin());
            assertEquals(1.5, domain.getCoefficientPrix());
        }

        @Test
        @DisplayName("Should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            Saison result = mapper.toDomain(null);
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("toEntity Tests")
    class ToEntityTests {

        @Test
        @DisplayName("Should convert domain to entity with id")
        void shouldConvertDomainToEntityWithId() {
            Saison domain = new Saison(1L, "Basse Saison", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31), 0.8);

            SaisonJpaEntity entity = mapper.toEntity(domain);

            assertEquals(1L, entity.getId());
            assertEquals("Basse Saison", entity.getNom());
            assertEquals(LocalDate.of(2024, 1, 1), entity.getDateDebut());
            assertEquals(LocalDate.of(2024, 3, 31), entity.getDateFin());
            assertEquals(0.8, entity.getCoefficientPrix());
        }

        @Test
        @DisplayName("Should not set id when domain id is null")
        void shouldNotSetIdWhenDomainIdIsNull() {
            Saison domain = new Saison(null, "Saison", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31), 1.0);

            SaisonJpaEntity entity = mapper.toEntity(domain);

            assertNull(entity.getId());
        }

        @Test
        @DisplayName("Should not set id when domain id is zero")
        void shouldNotSetIdWhenDomainIdIsZero() {
            Saison domain = new Saison(0L, "Saison", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31), 1.0);

            SaisonJpaEntity entity = mapper.toEntity(domain);

            assertNull(entity.getId());
        }

        @Test
        @DisplayName("Should return null when domain is null")
        void shouldReturnNullWhenDomainIsNull() {
            SaisonJpaEntity result = mapper.toEntity(null);
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Round Trip Tests")
    class RoundTripTests {

        @Test
        @DisplayName("Should preserve data through round trip conversion")
        void shouldPreserveDataThroughRoundTripConversion() {
            Saison original = new Saison(10L, "Noël", LocalDate.of(2024, 12, 20), LocalDate.of(2025, 1, 5), 2.0);

            SaisonJpaEntity entity = mapper.toEntity(original);
            Saison result = mapper.toDomain(entity);

            assertEquals(original.getId(), result.getId());
            assertEquals(original.getNom(), result.getNom());
            assertEquals(original.getDateDebut(), result.getDateDebut());
            assertEquals(original.getDateFin(), result.getDateFin());
            assertEquals(original.getCoefficientPrix(), result.getCoefficientPrix());
        }
    }
}
