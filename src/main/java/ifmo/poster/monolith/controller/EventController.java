package ifmo.poster.monolith.controller;

import ifmo.poster.monolith.dto.request.event.CreateEventRequest;
import ifmo.poster.monolith.dto.request.event.ModerateEventRequest;
import ifmo.poster.monolith.dto.request.event.UpdateEventRequest;
import ifmo.poster.monolith.dto.response.event.AdminEventResponse;
import ifmo.poster.monolith.dto.response.event.PublicEventDetailResponse;
import ifmo.poster.monolith.dto.response.event.PublicEventListResponse;
import ifmo.poster.monolith.enums.EventStatus;
import ifmo.poster.monolith.service.EventService;
import ifmo.poster.monolith.util.PageableUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<AdminEventResponse> create(@Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminEventResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        return ResponseEntity.ok(eventService.update(id, request));
    }

    @PatchMapping("/{id}/moderate")
    public ResponseEntity<AdminEventResponse> moderate(
            @PathVariable Long id,
            @Valid @RequestBody ModerateEventRequest request
    ) {
        return ResponseEntity.ok(eventService.moderate(id, request));
    }

    @GetMapping
    public ResponseEntity<Page<PublicEventListResponse>> getPublicPage(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PageableUtils.ensureMaxPageSize(pageable);
        Page<PublicEventListResponse> page = eventService.getPublicPage(pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @GetMapping("/feed")
    public ResponseEntity<Slice<PublicEventListResponse>> getPublicFeed(
            @RequestParam(required = false) Long afterId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        PageableUtils.ensureMaxPageSize(pageable);
        return ResponseEntity.ok(eventService.getPublicFeed(afterId, pageable));
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<AdminEventResponse>> getAdminPage(
            @RequestParam(required = false) EventStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PageableUtils.ensureMaxPageSize(pageable);
        Page<AdminEventResponse> page = eventService.getAdminPage(status, pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicEventDetailResponse> getPublicById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getPublicById(id));
    }

    @GetMapping("/{id}/admin")
    public ResponseEntity<AdminEventResponse> getAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getAdminById(id));
    }
}
