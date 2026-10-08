package ifmo.poster.monolith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ifmo.poster.monolith.enums.Role;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RoleAccessIntegrationTest extends AbstractIntegrationTest {

    @Test
    void protectedEndpointRequiresUserIdAndRole() throws Exception {
        mockMvc.perform(get("/api/users").param("size", "10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Query params userId and role are required"));
    }

    @Test
    void claimedRoleMustMatchDatabaseRole() throws Exception {
        Long userId = createUser();

        mockMvc.perform(withActor(get("/api/users/{id}", userId), userId, Role.ADMIN))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("does not match")));
    }

    @Test
    void userRoleIsRejectedOnAdminOnlyEndpoint() throws Exception {
        Long userId = createUser();

        mockMvc.perform(asUser(get("/api/users").param("size", "10"), userId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("not allowed")));
    }

    @Test
    void orderCreateRejectsBodyUserIdMismatch() throws Exception {
        Long buyerId = createUser();
        Long otherId = createUser();
        Long eventId = createActiveEvent("Role Order Event " + System.nanoTime());
        Long ticketTypeId = createTicketType();

        mockMvc.perform(asAdmin(post("/api/tickets/inventory")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "eventId", eventId,
                                "ticketTypeId", ticketTypeId,
                                "quantity", 1
                        )))))
                .andExpect(status().isCreated());

        mockMvc.perform(asUser(post("/api/orders")
                        .contentType(jsonContent())
                        .content(json(Map.of(
                                "userId", otherId,
                                "items", List.of(item(eventId, ticketTypeId, 1))
                        ))), buyerId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("body.userId must match query userId"));
    }

    @Test
    void allowedRoleCanAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(asAdmin(get("/api/users").param("size", "10")))
                .andExpect(status().isOk());
    }
}
