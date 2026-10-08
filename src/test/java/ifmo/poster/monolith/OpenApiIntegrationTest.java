package ifmo.poster.monolith;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

/** Проверка, что springdoc отдаёт OpenAPI JSON и Swagger UI. */
class OpenApiIntegrationTest extends AbstractIntegrationTest {

    /** /v3/api-docs содержит title и paths; /swagger-ui/index.html отдаёт UI. */
    @Test
    void openApiJsonAndSwaggerUiAreAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Poster Monolith API"))
                .andExpect(jsonPath("$.paths['/api/events']").exists());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Swagger UI")));
    }
}
