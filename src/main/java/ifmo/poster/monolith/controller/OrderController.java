package ifmo.poster.monolith.controller;

import ifmo.poster.monolith.dto.request.order.CreateOrderRequest;
import ifmo.poster.monolith.dto.response.order.OrderDetailResponse;
import ifmo.poster.monolith.dto.response.order.OrderSummaryResponse;
import ifmo.poster.monolith.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDetailResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @GetMapping("/{id}")
    public OrderDetailResponse getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping
    public ResponseEntity<Page<OrderSummaryResponse>> getByUser(
            @RequestParam Long userId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (pageable.getPageSize() > 50) {
            throw new IllegalArgumentException("Page size must be <= 50");
        }
        Page<OrderSummaryResponse> page = orderService.getByUser(userId, pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @PostMapping("/{id}/cancel")
    public OrderDetailResponse cancel(@PathVariable Long id) {
        return orderService.cancel(id);
    }
}