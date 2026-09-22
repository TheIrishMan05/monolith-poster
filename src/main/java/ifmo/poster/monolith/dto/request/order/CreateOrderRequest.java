package ifmo.poster.monolith.dto.request.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateOrderRequest {

    @NotNull(message = "ID пользователя обязательно")
    private Long userId;

    @NotNull 
    @Valid 
    private List<OrderItemDto> items;

    public static class OrderItemDto {
        @NotNull(message = "ID события обязательно")
        private Long eventId;

        @NotNull 
        private Long ticketTypeId;

        @Min(value = 1, message = "Минимум 1 билет")
        private int quantity;
    }
}
