package ifmo.poster.monolith.dto.response.order;

import ifmo.poster.monolith.dto.response.ticket.TicketResponse;
import ifmo.poster.monolith.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderDetailResponse {
    private Long id;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    
    private UserInfo user; 
    
    private List<OrderItemResponse> items;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String userName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long id;
        private String eventName;
        private String ticketTypeName;
        private int quantity;
        private BigDecimal pricePerTicket;
        private BigDecimal subtotal;
        private List<TicketResponse> tickets; 
    }
}
