package ifmo.poster.monolith.controller;

import ifmo.poster.monolith.dto.request.ticket.CreateTicketInventoryRequest;
import ifmo.poster.monolith.dto.response.ticket.TicketInventoryResponse;
import ifmo.poster.monolith.dto.response.ticket.TicketResponse;
import ifmo.poster.monolith.enums.TicketStatus;
import ifmo.poster.monolith.service.TicketService;
import ifmo.poster.monolith.util.PageableUtils;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/inventory")
    public ResponseEntity<TicketInventoryResponse> createInventory(
            @Valid @RequestBody CreateTicketInventoryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.createInventory(request));
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> listByEvent(
            @RequestParam Long eventId,
            @RequestParam(required = false) TicketStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PageableUtils.ensureMaxPageSize(pageable);
        Page<TicketResponse> page = ticketService.getByEvent(eventId, status, pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(page);
    }
}