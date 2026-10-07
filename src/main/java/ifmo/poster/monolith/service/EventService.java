package ifmo.poster.monolith.service;

import ifmo.poster.monolith.dto.request.event.CreateEventRequest;
import ifmo.poster.monolith.dto.request.event.ModerateEventRequest;
import ifmo.poster.monolith.dto.request.event.UpdateEventRequest;
import ifmo.poster.monolith.dto.response.common.TicketTypeAvailabilityDto;
import ifmo.poster.monolith.dto.response.event.AdminEventResponse;
import ifmo.poster.monolith.dto.response.event.PublicEventDetailResponse;
import ifmo.poster.monolith.dto.response.event.PublicEventListResponse;
import ifmo.poster.monolith.entity.Event;
import ifmo.poster.monolith.entity.Tag;
import ifmo.poster.monolith.entity.Ticket;
import ifmo.poster.monolith.enums.EventStatus;
import ifmo.poster.monolith.enums.TicketStatus;
import ifmo.poster.monolith.exception.BusinessException;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.repository.EventRepository;
import ifmo.poster.monolith.repository.TagRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TagRepository tagRepository;

    @Transactional
    public AdminEventResponse create(CreateEventRequest request) {
        Event event = new Event();
        event.setEventName(request.getEventName());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setDateTime(request.getDateTime());
        event.setStatus(EventStatus.PENDING);
        event.setTags(resolveTags(request.getTagNames()));
        return toAdminResponse(eventRepository.save(event));
    }

    @Transactional
    public AdminEventResponse update(Long id, UpdateEventRequest request) {
        Event event = findEvent(id);
        event.setEventName(request.getEventName());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        if (request.getDateTime() != null) {
            event.setDateTime(request.getDateTime());
        }
        return toAdminResponse(eventRepository.save(event));
    }

    @Transactional
    public AdminEventResponse moderate(Long id, ModerateEventRequest request) {
        Event event = findEvent(id);
        EventStatus newStatus = request.getNewStatus();

        if (newStatus != EventStatus.ACTIVE && newStatus != EventStatus.BLOCKED) {
            throw new BusinessException("Moderation allows only ACTIVE or BLOCKED");
        }
        if (newStatus == EventStatus.BLOCKED
                && (request.getReason() == null || request.getReason().isBlank())) {
            throw new BusinessException("Reason is required when blocking an event");
        }

        event.setStatus(newStatus);
        if (newStatus == EventStatus.BLOCKED) {
            event.setBlockReason(request.getReason());
        } else {
            // при разблокировке / APPROVE очищаем причину
            event.setBlockReason(null);
        }
        return toAdminResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public PublicEventDetailResponse getPublicById(Long id) {
        Event event = findEvent(id);
        if (event.getStatus() != EventStatus.ACTIVE) {
            throw new ResourceNotFoundException("Event not found: " + id);
        }
        return toDetailResponse(event);
    }

    @Transactional(readOnly = true)
    public AdminEventResponse getAdminById(Long id) {
        return toAdminResponse(findEvent(id));
    }

    /** Пагинация с total (потом в X-Total-Count). */
    @Transactional(readOnly = true)
    public Page<PublicEventListResponse> getPublicPage(Pageable pageable) {
        return eventRepository.findByStatus(EventStatus.ACTIVE, pageable)
                .map(this::toListResponse);
    }

    /** Infinite scroll без total count. */
    @Transactional(readOnly = true)
    public Slice<PublicEventListResponse> getPublicFeed(Long afterId, Pageable pageable) {
        Slice<Event> slice = (afterId == null)
                ? eventRepository.findByStatusOrderByIdAsc(EventStatus.ACTIVE, pageable)
                : eventRepository.findByStatusAndIdGreaterThanOrderByIdAsc(
                        EventStatus.ACTIVE, afterId, pageable);
        return slice.map(this::toListResponse);
    }

    @Transactional(readOnly = true)
    public Page<AdminEventResponse> getAdminPage(EventStatus status, Pageable pageable) {
        Page<Event> page = (status == null)
                ? eventRepository.findAll(pageable)
                : eventRepository.findByStatus(status, pageable);
        return page.map(this::toAdminResponse);
    }

    private Event findEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    private Set<Tag> resolveTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            if (name == null || name.isBlank()) {
                continue;
            }
            Tag tag = tagRepository.findByNameIgnoreCase(name.trim())
                    .orElseGet(() -> {
                        Tag created = new Tag();
                        created.setName(name.trim());
                        return tagRepository.save(created);
                    });
            tags.add(tag);
        }
        return tags;
    }

    private PublicEventListResponse toListResponse(Event event) {
        return PublicEventListResponse.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .dateTime(event.getDateTime())
                .location(event.getLocation())
                .minPrice(resolveMinPrice(event))
                .tags(tagNames(event))
                .build();
    }

    private PublicEventDetailResponse toDetailResponse(Event event) {
        return PublicEventDetailResponse.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .dateTime(event.getDateTime())
                .location(event.getLocation())
                .tags(tagNames(event))
                .availableTickets(buildAvailability(event))
                .build();
    }

    private AdminEventResponse toAdminResponse(Event event) {
        return AdminEventResponse.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .dateTime(event.getDateTime())
                .location(event.getLocation())
                .tags(tagNames(event))
                .status(event.getStatus())
                .blockReason(event.getBlockReason())
                .build();
    }

    private Set<String> tagNames(Event event) {
        return event.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }

    private BigDecimal resolveMinPrice(Event event) {
        if (event.getTickets() == null || event.getTickets().isEmpty()) {
            return null;
        }
        return event.getTickets().stream()
                .filter(t -> t.getStatus() == TicketStatus.AVAILABLE)
                .map(t -> t.getTicketType().getPrice())
                .min(BigDecimal::compareTo)
                .orElse(null);
    }

    private List<TicketTypeAvailabilityDto> buildAvailability(Event event) {
        if (event.getTickets() == null || event.getTickets().isEmpty()) {
            return List.of();
        }
        Map<Long, List<Ticket>> byType = event.getTickets().stream()
                .filter(t -> t.getStatus() == TicketStatus.AVAILABLE)
                .collect(Collectors.groupingBy(t -> t.getTicketType().getId()));

        return byType.values().stream()
                .map(tickets -> {
                    var type = tickets.getFirst().getTicketType();
                    int qty = tickets.size();
                    return TicketTypeAvailabilityDto.builder()
                            .ticketTypeId(type.getId())
                            .typeName(type.getTypeName())
                            .price(type.getPrice())
                            .availableQuantity(qty)
                            .isAvailableForPurchase(qty > 0)
                            .build();
                })
                .toList();
    }
}