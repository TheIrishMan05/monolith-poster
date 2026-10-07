package ifmo.poster.monolith.dto.request.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTicketTypeRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String typeName;

    @NotNull
    @Positive
    private BigDecimal price;
}