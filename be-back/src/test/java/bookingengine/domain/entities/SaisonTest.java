package bookingengine.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Saison Entity Tests")
class SaisonTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create saison with default constructor")
        void shouldCreateSaisonWithDefaultConstructor() {
            Saison saison = new Saison();

            assertNull(saison.getId());
            assertNull(saison.getNom());
            assertNull(saison.getDateDebut());
            assertNull(saison.getDateFin());
            assertEquals(0.0, saison.getCoefficientPrix());
        }

        @Test
        @DisplayName("Should create saison with all parameters")
        void shouldCreateSaisonWithAllParameters() {
            LocalDate debut = LocalDate.of(2024, 6, 1);
            LocalDate fin = LocalDate.of(2024, 8, 31);

            Saison saison = new Saison(1L, "Haute Saison Été", debut, fin, 1.5);

            assertEquals(1L, saison.getId());
            assertEquals("Haute Saison Été", saison.getNom());
            assertEquals(debut, saison.getDateDebut());
            assertEquals(fin, saison.getDateFin());
            assertEquals(1.5, saison.getCoefficientPrix());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void shouldSetAndGetId() {
            Saison saison = new Saison();
            saison.setId(10L);
            assertEquals(10L, saison.getId());
        }

        @Test
        @DisplayName("Should set and get nom")
        void shouldSetAndGetNom() {
            Saison saison = new Saison();
            saison.setNom("Basse Saison");
            assertEquals("Basse Saison", saison.getNom());
        }

        @Test
        @DisplayName("Should set and get dateDebut")
        void shouldSetAndGetDateDebut() {
            Saison saison = new Saison();
            LocalDate date = LocalDate.of(2024, 1, 1);
            saison.setDateDebut(date);
            assertEquals(date, saison.getDateDebut());
        }

        @Test
        @DisplayName("Should set and get dateFin")
        void shouldSetAndGetDateFin() {
            Saison saison = new Saison();
            LocalDate date = LocalDate.of(2024, 3, 31);
            saison.setDateFin(date);
            assertEquals(date, saison.getDateFin());
        }

        @Test
        @DisplayName("Should set and get coefficientPrix")
        void shouldSetAndGetCoefficientPrix() {
            Saison saison = new Saison();
            saison.setCoefficientPrix(0.8);
            assertEquals(0.8, saison.getCoefficientPrix());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle coefficient less than 1 (discount)")
        void shouldHandleCoefficientLessThanOne() {
            Saison saison = new Saison();
            saison.setCoefficientPrix(0.5);
            assertEquals(0.5, saison.getCoefficientPrix());
        }

        @Test
        @DisplayName("Should handle coefficient greater than 1 (surcharge)")
        void shouldHandleCoefficientGreaterThanOne() {
            Saison saison = new Saison();
            saison.setCoefficientPrix(2.0);
            assertEquals(2.0, saison.getCoefficientPrix());
        }

        @Test
        @DisplayName("Should handle same start and end date")
        void shouldHandleSameStartAndEndDate() {
            LocalDate date = LocalDate.of(2024, 12, 25);
            Saison saison = new Saison(1L, "Noël", date, date, 1.8);

            assertEquals(date, saison.getDateDebut());
            assertEquals(date, saison.getDateFin());
        }
    }
}
