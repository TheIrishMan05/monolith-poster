package ifmo.poster.monolith;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class TicketTypeApiIntegrationTest extends AbstractIntegrationTest {

    @Test
    void ticketTypeCrudAndValidationWork() throws Exception {
        String suffix = String.valueOf(System.nanoTime());
        MvcResult createResult = mockMvc.perform(asAdmin(post("/api/ticket-types")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "typeName", "Balcony " + suffix,
                                "price", BigDecimal.valueOf(750)
                        )))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.typeName").value("Balcony " + suffix))
                .andReturn();

        Long ticketTypeId = idFrom(createResult);
        Long readerId = createUser();

        mockMvc.perform(asUser(get("/api/ticket-types/{id}", ticketTypeId), readerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(750));

        mockMvc.perform(asAdmin(put("/api/ticket-types/{id}", ticketTypeId)
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "typeName", "Balcony Plus " + suffix,
                                "price", BigDecimal.valueOf(900)
                        )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeName").value("Balcony Plus " + suffix))
                .andExpect(jsonPath("$.price").value(900));

        mockMvc.perform(asUser(get("/api/ticket-types").param("size", "10"), readerId))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(asAdmin(post("/api/ticket-types")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "typeName", "Balcony Plus " + suffix,
                                "price", BigDecimal.valueOf(1000)
                        )))))
                .andExpect(status().isConflict());

        mockMvc.perform(asAdmin(delete("/api/ticket-types/{id}", ticketTypeId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(asUser(get("/api/ticket-types/{id}", ticketTypeId), readerId))
                .andExpect(status().isNotFound());
    }
}
