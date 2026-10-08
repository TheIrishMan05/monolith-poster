package ifmo.poster.monolith;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;

class UserApiIntegrationTest extends AbstractIntegrationTest {

    @Test
    void userCrudValidatesInputAndReturnsTotalCountHeader() throws Exception {
        String suffix = String.valueOf(System.nanoTime());

        var createResult = mockMvc.perform(post("/api/users")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "userName", "user_" + suffix,
                                "email", "user_" + suffix + "@poster.test"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn();

        Long userId = idFrom(createResult);

        mockMvc.perform(asUser(get("/api/users/{id}", userId), userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user_" + suffix + "@poster.test"));

        mockMvc.perform(asUser(patch("/api/users/{id}", userId)
                        .contentType(jsonContent())
                        .content(json(Map.of("email", "new_" + suffix + "@poster.test"))), userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new_" + suffix + "@poster.test"));

        mockMvc.perform(asAdmin(get("/api/users").param("size", "10")))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(post("/api/users")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "userName", "usr",
                                "email", "bad-email"
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
