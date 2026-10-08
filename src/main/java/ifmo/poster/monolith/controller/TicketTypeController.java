package ifmo.poster.monolith.controller;

import ifmo.poster.monolith.dto.request.ticket.CreateTicketTypeRequest;
import ifmo.poster.monolith.dto.request.ticket.UpdateTicketTypeRequest;
import ifmo.poster.monolith.dto.response.ticket.TicketTypeResponse;
import ifmo.poster.monolith.enums.Role;
import ifmo.poster.monolith.security.RequireRoles;
import ifmo.poster.monolith.service.TicketTypeService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ticket-types")
@RequiredArgsConstructor
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @RequireRoles({Role.ADMIN, Role.SUPERUSER})
    @PostMapping
    public ResponseEntity<TicketTypeResponse> create(@Valid @RequestBody CreateTicketTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketTypeService.create(request));
    }

    @RequireRoles({Role.USER, Role.ADMIN, Role.SUPERUSER, Role.CENSOR})
    @GetMapping("/{id}")
    public TicketTypeResponse getById(@PathVariable Long id) {
        return ticketTypeService.getById(id);
    }

    @RequireRoles({Role.USER, Role.ADMIN, Role.SUPERUSER, Role.CENSOR})
    @GetMapping
    public ResponseEntity<Page<TicketTypeResponse>> getAll(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PageableUtils.ensureMaxPageSize(pageable);
        Page<TicketTypeResponse> page = ticketTypeService.getAll(pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @RequireRoles({Role.ADMIN, Role.SUPERUSER})
    @PutMapping("/{id}")
    public TicketTypeResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketTypeRequest request
    ) {
        return ticketTypeService.update(id, request);
    }

    @RequireRoles({Role.ADMIN, Role.SUPERUSER})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ticketTypeService.delete(id);
    }
}
