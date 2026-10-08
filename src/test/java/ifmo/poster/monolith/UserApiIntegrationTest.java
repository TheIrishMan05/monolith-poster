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

/** CRUD пользователей, валидация и заголовок X-Total-Count на списке. */
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

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user_" + suffix + "@poster.test"));

        mockMvc.perform(patch("/api/users/{id}", userId)
                        .contentType(jsonContent())
                        .content(json(Map.of("email", "new_" + suffix + "@poster.test"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new_" + suffix + "@poster.test"));

        mockMvc.perform(get("/api/users").param("size", "10"))
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
