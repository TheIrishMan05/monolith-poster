package ifmo.poster.monolith.dto.response.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeAvailabilityDto {
    private Long ticketTypeId;
    private String typeName;
    private BigDecimal price;
    private int availableQuantity;
    private boolean isAvailableForPurchase;
}
