package ifmo.poster.monolith;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * База для интеграционных API-тестов: Spring Boot + MockMvc + PostgreSQL в Testcontainers.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("poster_test")
            .withUsername("poster")
            .withPassword("poster");

    static {
        POSTGRES.start();
    }

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.liquibase.enabled", () -> "true");
    }

    protected String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    protected Long idFrom(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    protected JsonNode bodyFrom(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    protected MediaType jsonContent() {
        return MediaType.APPLICATION_JSON;
    }

    protected Map<String, Object> item(Long eventId, Long ticketTypeId, int quantity) {
        return Map.of(
                "eventId", eventId,
                "ticketTypeId", ticketTypeId,
                "quantity", quantity
        );
    }

    protected Long createActiveEvent(String name) throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/events")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventName", name,
                                "description", "A long enough event description for validation and integration tests.",
                                "location", "Main Hall",
                                "dateTime", java.time.LocalDateTime.now().plusDays(5).toString(),
                                "tagNames", java.util.List.of("music", "live")
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        Long eventId = idFrom(createResult);

        mockMvc.perform(patch("/api/events/{id}/moderate", eventId)
                        .contentType(jsonContent())
                        .content(json(Map.of("newStatus", "ACTIVE"))))
                .andExpect(status().isOk());

        return eventId;
    }

    protected Long createUser() throws Exception {
        String suffix = String.valueOf(System.nanoTime());
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "userName", "user_" + suffix,
                                "email", "user_" + suffix + "@poster.test"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return idFrom(result);
    }

    protected Long createTicketType() throws Exception {
        String suffix = String.valueOf(System.nanoTime());
        MvcResult result = mockMvc.perform(post("/api/ticket-types")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "typeName", "Type " + suffix,
                                "price", BigDecimal.valueOf(1200)
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return idFrom(result);
    }
}
