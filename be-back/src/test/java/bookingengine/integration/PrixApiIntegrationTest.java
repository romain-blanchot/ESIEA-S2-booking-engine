package bookingengine.integration;

import bookingengine.TestcontainersConfiguration;
import bookingengine.adapters.persistence.repositories.ChambreJpaRepository;
import bookingengine.adapters.persistence.repositories.SaisonJpaRepository;
import bookingengine.adapters.web.dto.CalculPrixRequest;
import bookingengine.adapters.web.dto.ChambreDto;
import bookingengine.adapters.web.dto.SaisonDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DisplayName("Prix API Integration Tests")
class PrixApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChambreJpaRepository chambreJpaRepository;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setupObjectMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private SaisonJpaRepository saisonJpaRepository;

    @BeforeEach
    void setUp() {
        chambreJpaRepository.deleteAll();
        saisonJpaRepository.deleteAll();
    }

    private Long createChambre(String numero, double prixBase) throws Exception {
        ChambreDto chambre = new ChambreDto(null, numero, "Double", prixBase, 2, "Desc", true);
        MvcResult result = mockMvc.perform(post("/api/chambres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chambre)))
                .andExpect(status().isCreated())
                .andReturn();

        ChambreDto created = objectMapper.readValue(
                result.getResponse().getContentAsString(), ChambreDto.class);
        return created.id();
    }

    private void createSaison(String nom, LocalDate debut, LocalDate fin, double coefficient) throws Exception {
        SaisonDto saison = new SaisonDto(null, nom, debut, fin, coefficient);
        mockMvc.perform(post("/api/saisons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saison)))
                .andExpect(status().isCreated());
    }

    @Nested
    @DisplayName("POST /api/prix/calculer")
    @WithMockUser
    class CalculerPrixTests {

        @Test
        @DisplayName("Should calculate price without season")
        void shouldCalculatePriceWithoutSeason() throws Exception {
            Long chambreId = createChambre("101", 100.0);

            CalculPrixRequest request = new CalculPrixRequest(
                    chambreId,
                    LocalDate.of(2024, 5, 1),
                    LocalDate.of(2024, 5, 4)
            );

            mockMvc.perform(post("/api/prix/calculer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numeroChambre").value("101"))
                    .andExpect(jsonPath("$.nombreNuits").value(3))
                    .andExpect(jsonPath("$.prixTotal").value(300.0));
        }

        @Test
        @DisplayName("Should calculate price with season coefficient")
        void shouldCalculatePriceWithSeasonCoefficient() throws Exception {
            Long chambreId = createChambre("101", 100.0);
            createSaison("Haute Saison", LocalDate.of(2024, 7, 1), LocalDate.of(2024, 8, 31), 1.5);

            CalculPrixRequest request = new CalculPrixRequest(
                    chambreId,
                    LocalDate.of(2024, 7, 15),
                    LocalDate.of(2024, 7, 17)
            );

            mockMvc.perform(post("/api/prix/calculer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombreNuits").value(2))
                    .andExpect(jsonPath("$.prixTotal").value(300.0))
                    .andExpect(jsonPath("$.coefficientSaisonnier").value(1.5));
        }

        @Test
        @DisplayName("Should calculate mixed season and non-season price")
        void shouldCalculateMixedSeasonAndNonSeasonPrice() throws Exception {
            Long chambreId = createChambre("101", 100.0);
            createSaison("Haute Saison", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31), 1.5);

            CalculPrixRequest request = new CalculPrixRequest(
                    chambreId,
                    LocalDate.of(2024, 5, 30),
                    LocalDate.of(2024, 6, 2)
            );

            mockMvc.perform(post("/api/prix/calculer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombreNuits").value(3))
                    .andExpect(jsonPath("$.prixTotal").value(350.0))
                    .andExpect(jsonPath("$.detailsParJour.length()").value(3));
        }

        @Test
        @DisplayName("Should return detailed breakdown per day")
        void shouldReturnDetailedBreakdownPerDay() throws Exception {
            Long chambreId = createChambre("101", 100.0);

            CalculPrixRequest request = new CalculPrixRequest(
                    chambreId,
                    LocalDate.of(2024, 5, 1),
                    LocalDate.of(2024, 5, 3)
            );

            mockMvc.perform(post("/api/prix/calculer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.detailsParJour.length()").value(2))
                    .andExpect(jsonPath("$.detailsParJour[0].date").value("2024-05-01"))
                    .andExpect(jsonPath("$.detailsParJour[0].saison").value("Hors saison"))
                    .andExpect(jsonPath("$.detailsParJour[0].coefficient").value(1.0))
                    .andExpect(jsonPath("$.detailsParJour[0].prix").value(100.0));
        }

        @Test
        @DisplayName("Should return 404 when chambre not found")
        void shouldReturn404WhenChambreNotFound() throws Exception {
            CalculPrixRequest request = new CalculPrixRequest(
                    999L,
                    LocalDate.of(2024, 5, 1),
                    LocalDate.of(2024, 5, 3)
            );

            mockMvc.perform(post("/api/prix/calculer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when dates are invalid")
        void shouldReturn400WhenDatesAreInvalid() throws Exception {
            Long chambreId = createChambre("101", 100.0);

            CalculPrixRequest request = new CalculPrixRequest(
                    chambreId,
                    LocalDate.of(2024, 5, 10),
                    LocalDate.of(2024, 5, 5)
            );

            mockMvc.perform(post("/api/prix/calculer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
