package bookingengine.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Chambre Entity Tests")
class ChambreTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create chambre with default constructor")
        void shouldCreateChambreWithDefaultConstructor() {
            Chambre chambre = new Chambre();

            assertNull(chambre.getId());
            assertNull(chambre.getNumero());
            assertNull(chambre.getType());
            assertEquals(0.0, chambre.getPrixBase());
            assertEquals(0, chambre.getCapacite());
            assertNull(chambre.getDescription());
            assertFalse(chambre.isDisponible());
        }

        @Test
        @DisplayName("Should create chambre with all parameters")
        void shouldCreateChambreWithAllParameters() {
            Chambre chambre = new Chambre(1L, "101", "Double", 89.99, 2, "Chambre double vue jardin", true);

            assertEquals(1L, chambre.getId());
            assertEquals("101", chambre.getNumero());
            assertEquals("Double", chambre.getType());
            assertEquals(89.99, chambre.getPrixBase());
            assertEquals(2, chambre.getCapacite());
            assertEquals("Chambre double vue jardin", chambre.getDescription());
            assertTrue(chambre.isDisponible());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void shouldSetAndGetId() {
            Chambre chambre = new Chambre();
            chambre.setId(42L);
            assertEquals(42L, chambre.getId());
        }

        @Test
        @DisplayName("Should set and get numero")
        void shouldSetAndGetNumero() {
            Chambre chambre = new Chambre();
            chambre.setNumero("205");
            assertEquals("205", chambre.getNumero());
        }

        @Test
        @DisplayName("Should set and get type")
        void shouldSetAndGetType() {
            Chambre chambre = new Chambre();
            chambre.setType("Suite");
            assertEquals("Suite", chambre.getType());
        }

        @Test
        @DisplayName("Should set and get prixBase")
        void shouldSetAndGetPrixBase() {
            Chambre chambre = new Chambre();
            chambre.setPrixBase(150.50);
            assertEquals(150.50, chambre.getPrixBase());
        }

        @Test
        @DisplayName("Should set and get capacite")
        void shouldSetAndGetCapacite() {
            Chambre chambre = new Chambre();
            chambre.setCapacite(4);
            assertEquals(4, chambre.getCapacite());
        }

        @Test
        @DisplayName("Should set and get description")
        void shouldSetAndGetDescription() {
            Chambre chambre = new Chambre();
            chambre.setDescription("Belle chambre avec vue mer");
            assertEquals("Belle chambre avec vue mer", chambre.getDescription());
        }

        @Test
        @DisplayName("Should set and get disponible")
        void shouldSetAndGetDisponible() {
            Chambre chambre = new Chambre();
            assertFalse(chambre.isDisponible());

            chambre.setDisponible(true);
            assertTrue(chambre.isDisponible());

            chambre.setDisponible(false);
            assertFalse(chambre.isDisponible());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null values")
        void shouldHandleNullValues() {
            Chambre chambre = new Chambre(null, null, null, 0, 0, null, false);

            assertNull(chambre.getId());
            assertNull(chambre.getNumero());
            assertNull(chambre.getType());
            assertNull(chambre.getDescription());
        }

        @Test
        @DisplayName("Should handle negative prixBase")
        void shouldHandleNegativePrixBase() {
            Chambre chambre = new Chambre();
            chambre.setPrixBase(-50.0);
            assertEquals(-50.0, chambre.getPrixBase());
        }

        @Test
        @DisplayName("Should handle zero capacite")
        void shouldHandleZeroCapacite() {
            Chambre chambre = new Chambre();
            chambre.setCapacite(0);
            assertEquals(0, chambre.getCapacite());
        }
    }
}
