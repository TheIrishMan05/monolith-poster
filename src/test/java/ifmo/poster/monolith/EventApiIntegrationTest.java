package ifmo.poster.monolith;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

/** Публичные события: page с X-Total-Count, infinite feed без totalElements, get by id. */
class EventApiIntegrationTest extends AbstractIntegrationTest {

    /** ACTIVE событие видно в /api/events, /feed (Slice) и по id. */
    @Test
    void eventsSupportModerationPageAndInfiniteFeed() throws Exception {
        Long eventId = createActiveEvent("Concert " + System.nanoTime());

        mockMvc.perform(get("/api/events").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/events/feed").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.page.totalElements").doesNotExist());

        mockMvc.perform(get("/api/events/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId));
    }

    /** size=51 на публичном списке событий → 400. */
    @Test
    void pageSizeIsLimitedToFiftyRecords() throws Exception {
        mockMvc.perform(get("/api/events").param("size", "51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page size must be <= 50"));
    }

}
