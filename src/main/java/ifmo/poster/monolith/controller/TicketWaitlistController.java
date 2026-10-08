package ifmo.poster.monolith.controller;

import ifmo.poster.monolith.dto.request.ticket.JoinTicketWaitlistRequest;
import ifmo.poster.monolith.dto.response.ticket.TicketWaitlistResponse;
import ifmo.poster.monolith.enums.Role;
import ifmo.poster.monolith.security.RequireRoles;
import ifmo.poster.monolith.service.TicketWaitlistService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ticket-waitlist")
@RequiredArgsConstructor
@RequireRoles({Role.USER, Role.ADMIN, Role.SUPERUSER, Role.CENSOR})
public class TicketWaitlistController {

    private final TicketWaitlistService waitlistService;

    @PostMapping
    public ResponseEntity<TicketWaitlistResponse> join(
            @RequestParam Long userId,
            @Valid @RequestBody JoinTicketWaitlistRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(waitlistService.join(userId, request));
    }

    @GetMapping
    public ResponseEntity<Page<TicketWaitlistResponse>> listMine(
            @RequestParam Long userId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageableUtils.ensureMaxPageSize(pageable);
        Page<TicketWaitlistResponse> page = waitlistService.getByUser(userId, pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @PostMapping("/{id}/cancel")
    public TicketWaitlistResponse cancel(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        return waitlistService.cancel(id, userId);
    }
}
