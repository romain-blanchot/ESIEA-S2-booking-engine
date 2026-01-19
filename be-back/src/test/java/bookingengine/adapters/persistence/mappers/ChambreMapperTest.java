package bookingengine.adapters.persistence.mappers;

import bookingengine.adapters.persistence.entities.ChambreJpaEntity;
import bookingengine.domain.entities.Chambre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChambreMapper Tests")
class ChambreMapperTest {

    private ChambreMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ChambreMapper();
    }

    @Nested
    @DisplayName("toDomain Tests")
    class ToDomainTests {

        @Test
        @DisplayName("Should convert entity to domain")
        void shouldConvertEntityToDomain() {
            ChambreJpaEntity entity = new ChambreJpaEntity();
            entity.setId(1L);
            entity.setNumero("101");
            entity.setType("Double");
            entity.setPrixBase(89.99);
            entity.setCapacite(2);
            entity.setDescription("Chambre avec vue jardin");
            entity.setDisponible(true);

            Chambre domain = mapper.toDomain(entity);

            assertEquals(1L, domain.getId());
            assertEquals("101", domain.getNumero());
            assertEquals("Double", domain.getType());
            assertEquals(89.99, domain.getPrixBase());
            assertEquals(2, domain.getCapacite());
            assertEquals("Chambre avec vue jardin", domain.getDescription());
            assertTrue(domain.isDisponible());
        }

        @Test
        @DisplayName("Should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            Chambre result = mapper.toDomain(null);
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("toEntity Tests")
    class ToEntityTests {

        @Test
        @DisplayName("Should convert domain to entity with id")
        void shouldConvertDomainToEntityWithId() {
            Chambre domain = new Chambre(1L, "101", "Double", 89.99, 2, "Description", true);

            ChambreJpaEntity entity = mapper.toEntity(domain);

            assertEquals(1L, entity.getId());
            assertEquals("101", entity.getNumero());
            assertEquals("Double", entity.getType());
            assertEquals(89.99, entity.getPrixBase());
            assertEquals(2, entity.getCapacite());
            assertEquals("Description", entity.getDescription());
            assertTrue(entity.isDisponible());
        }

        @Test
        @DisplayName("Should not set id when domain id is null")
        void shouldNotSetIdWhenDomainIdIsNull() {
            Chambre domain = new Chambre(null, "101", "Double", 89.99, 2, "Description", true);

            ChambreJpaEntity entity = mapper.toEntity(domain);

            assertNull(entity.getId());
        }

        @Test
        @DisplayName("Should not set id when domain id is zero")
        void shouldNotSetIdWhenDomainIdIsZero() {
            Chambre domain = new Chambre(0L, "101", "Double", 89.99, 2, "Description", true);

            ChambreJpaEntity entity = mapper.toEntity(domain);

            assertNull(entity.getId());
        }

        @Test
        @DisplayName("Should return null when domain is null")
        void shouldReturnNullWhenDomainIsNull() {
            ChambreJpaEntity result = mapper.toEntity(null);
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Round Trip Tests")
    class RoundTripTests {

        @Test
        @DisplayName("Should preserve data through round trip conversion")
        void shouldPreserveDataThroughRoundTripConversion() {
            Chambre original = new Chambre(5L, "505", "Suite", 250.0, 4, "Suite de luxe", true);

            ChambreJpaEntity entity = mapper.toEntity(original);
            Chambre result = mapper.toDomain(entity);

            assertEquals(original.getId(), result.getId());
            assertEquals(original.getNumero(), result.getNumero());
            assertEquals(original.getType(), result.getType());
            assertEquals(original.getPrixBase(), result.getPrixBase());
            assertEquals(original.getCapacite(), result.getCapacite());
            assertEquals(original.getDescription(), result.getDescription());
            assertEquals(original.isDisponible(), result.isDisponible());
        }
    }
}
