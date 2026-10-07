package ifmo.poster.monolith.service;

import ifmo.poster.monolith.dto.request.order.CreateOrderRequest;
import ifmo.poster.monolith.dto.response.order.OrderDetailResponse;
import ifmo.poster.monolith.dto.response.order.OrderSummaryResponse;
import ifmo.poster.monolith.dto.response.ticket.TicketResponse;
import ifmo.poster.monolith.entity.Event;
import ifmo.poster.monolith.entity.Order;
import ifmo.poster.monolith.entity.OrderItem;
import ifmo.poster.monolith.entity.Ticket;
import ifmo.poster.monolith.entity.TicketType;
import ifmo.poster.monolith.entity.User;
import ifmo.poster.monolith.enums.EventStatus;
import ifmo.poster.monolith.enums.OrderStatus;
import ifmo.poster.monolith.enums.TicketStatus;
import ifmo.poster.monolith.exception.BusinessException;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.payment.FakePaymentService;
import ifmo.poster.monolith.payment.PaymentRequest;
import ifmo.poster.monolith.payment.PaymentResult;
import ifmo.poster.monolith.repository.EventRepository;
import ifmo.poster.monolith.repository.OrderRepository;
import ifmo.poster.monolith.repository.TicketRepository;
import ifmo.poster.monolith.repository.TicketTypeRepository;
import ifmo.poster.monolith.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final FakePaymentService paymentService;

    /**
     * Транзакция №1: заказ + позиции + билеты + оплата.
     * Билеты выбираются с PESSIMISTIC_WRITE после save order items.
     */
    @Transactional
    public OrderDetailResponse create(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("Order must contain at least one item");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + request.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderRequest.OrderItemDto itemDto : request.getItems()) {
            Event event = eventRepository.findById(itemDto.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Event not found: " + itemDto.getEventId()));
            if (event.getStatus() != EventStatus.ACTIVE) {
                throw new BusinessException(
                        "Event is not available for purchase: " + event.getId());
            }

            TicketType ticketType = ticketTypeRepository.findById(itemDto.getTicketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ticket type not found: " + itemDto.getTicketTypeId()));

            long availableCount = ticketRepository.countByEventIdAndTicketTypeIdAndStatus(
                    event.getId(),
                    ticketType.getId(),
                    TicketStatus.AVAILABLE
            );
            if (availableCount < itemDto.getQuantity()) {
                throw new BusinessException(
                        "Not enough tickets for event " + event.getId()
                                + ", type " + ticketType.getTypeName());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setEvent(event);
            item.setTicketType(ticketType);
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(ticketType.getPrice());
            order.getItems().add(item);

            total = total.add(ticketType.getPrice()
                    .multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        Order saved = orderRepository.save(order);

        for (OrderItem item : saved.getItems()) {
            List<Ticket> available = ticketRepository.findAvailableForUpdate(
                    item.getEvent().getId(),
                    item.getTicketType().getId(),
                    TicketStatus.AVAILABLE,
                    PageRequest.of(0, item.getQuantity())
            );
            if (available.size() < item.getQuantity()) {
                throw new BusinessException(
                        "Not enough tickets for event " + item.getEvent().getId()
                                + ", type " + item.getTicketType().getTypeName());
            }

            for (Ticket ticket : available) {
                ticket.setOrderItem(item);
                ticket.setStatus(TicketStatus.SOLD);
                ticketRepository.save(ticket);
            }
        }

        PaymentResult payment = paymentService.pay(
                new PaymentRequest(saved.getId(), user.getId(), total)
        );
        if (!payment.success()) {
            throw new BusinessException("Payment failed: " + payment.message());
        }

        saved.setStatus(OrderStatus.CONFIRMED);
        return toDetail(orderRepository.save(saved));
    }

    /**
     * Транзакция №2: отмена + возврат билетов + refund.
     */
    @Transactional
    public OrderDetailResponse cancel(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.CONFIRMED
                && order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Order cannot be cancelled: " + order.getStatus());
        }

        BigDecimal total = calcTotal(order);

        PaymentResult refund = paymentService.refund(
                new PaymentRequest(order.getId(), order.getUser().getId(), total)
        );
        if (!refund.success()) {
            throw new BusinessException("Refund failed: " + refund.message());
        }

        List<Ticket> tickets = ticketRepository.findByOrderItem_Order_Id(order.getId());
        for (Ticket ticket : tickets) {
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setOrderItem(null);
            ticketRepository.save(ticket);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return toDetail(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getById(Long id) {
        return toDetail(orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id)));
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return orderRepository.findByUserId(userId, pageable).map(this::toSummary);
    }

    private BigDecimal calcTotal(Order order) {
        return order.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderSummaryResponse toSummary(Order order) {
        return OrderSummaryResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalAmount(calcTotal(order))
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderDetailResponse toDetail(Order order) {
        List<OrderDetailResponse.OrderItemResponse> items = order.getItems().stream()
                .map(item -> {
                    // билеты грузим из репо: коллекция item.tickets после create может быть пустой
                    List<TicketResponse> tickets = ticketRepository
                            .findByOrderItem_Id(item.getId())
                            .stream()
                            .map(this::toTicketResponse)
                            .toList();
                    BigDecimal subtotal = item.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new OrderDetailResponse.OrderItemResponse(
                            item.getId(),
                            item.getEvent().getEventName(),
                            item.getTicketType().getTypeName(),
                            item.getQuantity(),
                            item.getPrice(),
                            subtotal,
                            tickets
                    );
                })
                .toList();

        return OrderDetailResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .totalAmount(calcTotal(order))
                .user(new OrderDetailResponse.UserInfo(
                        order.getUser().getId(),
                        order.getUser().getUserName()))
                .items(items)
                .build();
    }

    private TicketResponse toTicketResponse(Ticket ticket) {
        TicketResponse.SeatInfo seat = null;
        if (ticket.getSeat() != null) {
            seat = new TicketResponse.SeatInfo(
                    ticket.getSeat().getRow(),
                    ticket.getSeat().getNumber()
            );
        }
        return TicketResponse.builder()
                .id(ticket.getId())
                .eventName(ticket.getEvent().getEventName())
                .eventDateTime(ticket.getEvent().getDateTime())
                .ticketTypeName(ticket.getTicketType().getTypeName())
                .status(ticket.getStatus())
                .seat(seat)
                .build();
    }
}