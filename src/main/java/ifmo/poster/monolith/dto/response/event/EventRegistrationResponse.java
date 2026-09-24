package ifmo.poster.monolith.dto.response.event;

import ifmo.poster.monolith.dto.response.common.EventBriefDto;
import ifmo.poster.monolith.dto.response.user.UserResponse;
import ifmo.poster.monolith.enums.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationResponse {
    private Long id;
    private EventBriefDto event;
    private UserResponse user; 
    private LocalDateTime registrationDate;
    private RegistrationStatus status;
}