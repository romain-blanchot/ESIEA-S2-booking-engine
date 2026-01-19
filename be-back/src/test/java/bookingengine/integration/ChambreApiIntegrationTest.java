package bookingengine.integration;

import bookingengine.TestcontainersConfiguration;
import bookingengine.adapters.persistence.repositories.ChambreJpaRepository;
import bookingengine.adapters.web.dto.ChambreDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DisplayName("Chambre API Integration Tests")
class ChambreApiIntegrationTest {

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

    @BeforeEach
    void setUp() {
        chambreJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("Full CRUD Flow Tests")
    @WithMockUser
    class FullCrudFlowTests {

        @Test
        @DisplayName("Should perform complete CRUD operations")
        void shouldPerformCompleteCrudOperations() throws Exception {
            // CREATE
            ChambreDto newChambre = new ChambreDto(null, "101", "Double", 89.99, 2, "Chambre test", true);

            MvcResult createResult = mockMvc.perform(post("/api/chambres")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newChambre)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.numero").value("101"))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andReturn();

            ChambreDto created = objectMapper.readValue(
                    createResult.getResponse().getContentAsString(), ChambreDto.class);
            Long createdId = created.id();

            // READ
            mockMvc.perform(get("/api/chambres/" + createdId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numero").value("101"))
                    .andExpect(jsonPath("$.type").value("Double"));

            // UPDATE
            ChambreDto updatedChambre = new ChambreDto(createdId, "101", "Suite", 150.0, 3, "Suite mise à jour", true);

            mockMvc.perform(put("/api/chambres/" + createdId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedChambre)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.type").value("Suite"))
                    .andExpect(jsonPath("$.prixBase").value(150.0));

            // DELETE
            mockMvc.perform(delete("/api/chambres/" + createdId))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/chambres/" + createdId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/chambres")
    @WithMockUser
    class GetAllChambresTests {

        @Test
        @DisplayName("Should return empty list initially")
        void shouldReturnEmptyListInitially() throws Exception {
            mockMvc.perform(get("/api/chambres"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return all created chambres")
        void shouldReturnAllCreatedChambres() throws Exception {
            // Create first chambre
            mockMvc.perform(post("/api/chambres")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new ChambreDto(null, "101", "Double", 89.99, 2, "Desc 1", true))));

            // Create second chambre
            mockMvc.perform(post("/api/chambres")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new ChambreDto(null, "102", "Simple", 59.99, 1, "Desc 2", true))));

            mockMvc.perform(get("/api/chambres"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/chambres/disponibles")
    @WithMockUser
    class GetChambresDisponiblesTests {

        @Test
        @DisplayName("Should return only available chambres")
        void shouldReturnOnlyAvailableChambres() throws Exception {
            // Create available chambre
            mockMvc.perform(post("/api/chambres")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new ChambreDto(null, "101", "Double", 89.99, 2, "Desc", true))));

            // Create unavailable chambre
            mockMvc.perform(post("/api/chambres")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new ChambreDto(null, "102", "Simple", 59.99, 1, "Desc", false))));

            mockMvc.perform(get("/api/chambres/disponibles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].disponible").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/chambres/type/{type}")
    @WithMockUser
    class GetChambresByTypeTests {

        @Test
        @DisplayName("Should return chambres filtered by type")
        void shouldReturnChambresFilteredByType() throws Exception {
            mockMvc.perform(post("/api/chambres")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new ChambreDto(null, "101", "Double", 89.99, 2, "Desc", true))));

            mockMvc.perform(post("/api/chambres")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new ChambreDto(null, "102", "Suite", 199.99, 4, "Desc", true))));

            mockMvc.perform(post("/api/chambres")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new ChambreDto(null, "201", "Double", 99.99, 2, "Desc", true))));

            mockMvc.perform(get("/api/chambres/type/Double"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    @WithMockUser
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should return 404 for non-existent chambre")
        void shouldReturn404ForNonExistentChambre() throws Exception {
            mockMvc.perform(get("/api/chambres/999"))
                    .andExpect(status().isNotFound());
        }
    }
}
