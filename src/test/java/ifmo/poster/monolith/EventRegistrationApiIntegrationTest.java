package ifmo.poster.monolith;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class EventRegistrationApiIntegrationTest extends AbstractIntegrationTest {

    @Test
    void registrationCanBeCreatedListedCanceledAndCreatedAgain() throws Exception {
        Long userId = createUser();
        Long eventId = createActiveEvent("Registration Event " + System.nanoTime());

        MvcResult registerResult = mockMvc.perform(post("/api/registrations")
                        .param("userId", String.valueOf(userId))
                        .contentType(jsonContent())
                        .content(json(Map.of("eventId", eventId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.event.id").value(eventId))
                .andExpect(jsonPath("$.user.id").value(userId))
                .andReturn();

        Long registrationId = idFrom(registerResult);

        mockMvc.perform(post("/api/registrations")
                        .param("userId", String.valueOf(userId))
                        .contentType(jsonContent())
                        .content(json(Map.of("eventId", eventId))))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/registrations/{id}", registrationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(registrationId));

        mockMvc.perform(get("/api/registrations")
                        .param("userId", String.valueOf(userId))
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/registrations")
                        .param("eventId", String.valueOf(eventId))
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(post("/api/registrations/{id}/cancel", registrationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));

        mockMvc.perform(post("/api/registrations/{id}/cancel", registrationId))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/registrations")
                        .param("userId", String.valueOf(userId))
                        .contentType(jsonContent())
                        .content(json(Map.of("eventId", eventId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void registrationRejectsInvalidListFiltersAndInactiveEvent() throws Exception {
        Long userId = createUser();

        MvcResult createResult = mockMvc.perform(post("/api/events")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventName", "Pending Event " + System.nanoTime(),
                                "description", "A long enough event description for pending registration test.",
                                "location", "Small Hall",
                                "dateTime", java.time.LocalDateTime.now().plusDays(3).toString()
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        Long pendingEventId = idFrom(createResult);

        mockMvc.perform(post("/api/registrations")
                        .param("userId", String.valueOf(userId))
                        .contentType(jsonContent())
                        .content(json(Map.of("eventId", pendingEventId))))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/registrations"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/registrations")
                        .param("userId", String.valueOf(userId))
                        .param("eventId", String.valueOf(pendingEventId)))
                .andExpect(status().isBadRequest());
    }
}
