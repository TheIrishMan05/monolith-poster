package ifmo.poster.monolith.dto.request.event;

import ifmo.poster.monolith.enums.EventStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ModerateEventRequest {
  
  @NotNull(message="Обязательно указать новый статус")
  private EventStatus newStatus;

  @Size(max = 500)
  private String reason;
  
}
