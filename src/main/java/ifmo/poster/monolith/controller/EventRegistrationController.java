package ifmo.poster.monolith.controller;

import ifmo.poster.monolith.dto.request.event.CreateEventRegistrationRequest;
import ifmo.poster.monolith.dto.response.event.EventRegistrationResponse;
import ifmo.poster.monolith.service.EventRegistrationService;
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
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService registrationService;

    @PostMapping
    public ResponseEntity<EventRegistrationResponse> register(
            @RequestParam Long userId,
            @Valid @RequestBody CreateEventRegistrationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.register(userId, request));
    }

    @GetMapping("/{id}")
    public EventRegistrationResponse getById(@PathVariable Long id) {
        return registrationService.getById(id);
    }

    @GetMapping
    public ResponseEntity<Page<EventRegistrationResponse>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long eventId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (pageable.getPageSize() > 50) {
            throw new IllegalArgumentException("Page size must be <= 50");
        }
        if (userId == null && eventId == null) {
            throw new IllegalArgumentException("Specify userId or eventId");
        }
        if (userId != null && eventId != null) {
            throw new IllegalArgumentException("Specify only one of userId or eventId");
        }

        Page<EventRegistrationResponse> page = (userId != null)
                ? registrationService.getByUser(userId, pageable)
                : registrationService.getByEvent(eventId, pageable);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @PostMapping("/{id}/cancel")
    public EventRegistrationResponse cancel(@PathVariable Long id) {
        return registrationService.cancel(id);
    }
}