package ifmo.poster.monolith.controller;

import ifmo.poster.monolith.dto.response.notification.NotificationResponse;
import ifmo.poster.monolith.service.NotificationService;
import ifmo.poster.monolith.util.PageableUtils;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> list(
            @RequestParam Long userId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageableUtils.ensureMaxPageSize(pageable);
        Page<NotificationResponse> page = notificationService.getByUser(userId, pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @PostMapping("/{id}/read")
    public NotificationResponse markRead(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        return notificationService.markRead(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        notificationService.delete(id, userId);
    }

    @DeleteMapping
    public Map<String, Long> deleteAll(@RequestParam Long userId) {
        long deleted = notificationService.deleteAllForUser(userId);
        return Map.of("deleted", deleted);
    }
}
