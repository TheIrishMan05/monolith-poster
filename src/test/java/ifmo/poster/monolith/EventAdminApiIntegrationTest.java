package ifmo.poster.monolith;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Админские операции с событиями: чтение, обновление, блокировка, скрытие из публичного API. */
class EventAdminApiIntegrationTest extends AbstractIntegrationTest {

    @Test
    void adminCanReadUpdateAndBlockEvents() throws Exception {
        Long eventId = createActiveEvent("Admin Event " + System.nanoTime());

        mockMvc.perform(get("/api/events/{id}/admin", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId));

        mockMvc.perform(put("/api/events/{id}", eventId)
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventName", "Updated Admin Event",
                                "description", "A long enough updated event description for admin test.",
                                "location", "Updated Hall",
                                "dateTime", LocalDateTime.now().plusDays(7).toString()
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Updated Admin Event"))
                .andExpect(jsonPath("$.location").value("Updated Hall"));

        mockMvc.perform(get("/api/events/admin")
                        .param("status", "ACTIVE")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(patch("/api/events/{id}/moderate", eventId)
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "newStatus", "BLOCKED",
                                "reason", "Manual moderation"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"))
                .andExpect(jsonPath("$.blockReason").value("Manual moderation"));

        mockMvc.perform(get("/api/events/{id}", eventId))
                .andExpect(status().isNotFound());
    }
}
