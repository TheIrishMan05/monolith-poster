package ifmo.poster.monolith.service;

import ifmo.poster.monolith.dto.request.ticket.JoinTicketWaitlistRequest;
import ifmo.poster.monolith.dto.response.ticket.TicketWaitlistResponse;
import ifmo.poster.monolith.entity.Event;
import ifmo.poster.monolith.entity.TicketType;
import ifmo.poster.monolith.entity.TicketWaitlist;
import ifmo.poster.monolith.entity.User;
import ifmo.poster.monolith.enums.EventStatus;
import ifmo.poster.monolith.enums.TicketStatus;
import ifmo.poster.monolith.enums.WaitlistStatus;
import ifmo.poster.monolith.exception.BusinessException;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.repository.EventRepository;
import ifmo.poster.monolith.repository.TicketRepository;
import ifmo.poster.monolith.repository.TicketTypeRepository;
import ifmo.poster.monolith.repository.TicketWaitlistRepository;
import ifmo.poster.monolith.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketWaitlistService {

    private final TicketWaitlistRepository waitlistRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public TicketWaitlistResponse join(Long userId, JoinTicketWaitlistRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found: " + request.getEventId()));
        if (event.getStatus() != EventStatus.ACTIVE) {
            throw new BusinessException("Event is not open for waitlist: " + event.getId());
        }

        TicketType ticketType = ticketTypeRepository.findById(request.getTicketTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket type not found: " + request.getTicketTypeId()));

        long available = ticketRepository.countByEventIdAndTicketTypeIdAndStatus(
                event.getId(),
                ticketType.getId(),
                TicketStatus.AVAILABLE
        );
        if (available > 0) {
            throw new BusinessException(
                    "Tickets are already available — place an order instead of joining waitlist");
        }

        Optional<TicketWaitlist> existing = waitlistRepository
                .findByUserIdAndEventIdAndTicketTypeIdAndStatus(
                        userId,
                        event.getId(),
                        ticketType.getId(),
                        WaitlistStatus.WAITING
                );
        if (existing.isPresent()) {
            throw new BusinessException("Already on waitlist for this event and ticket type");
        }

        Optional<TicketWaitlist> cancelled = waitlistRepository
                .findByUserIdAndEventIdAndTicketTypeIdAndStatus(
                        userId,
                        event.getId(),
                        ticketType.getId(),
                        WaitlistStatus.CANCELLED
                );
        if (cancelled.isPresent()) {
            TicketWaitlist reopened = cancelled.get();
            reopened.setStatus(WaitlistStatus.WAITING);
            return toResponse(waitlistRepository.save(reopened));
        }

        return toResponse(waitlistRepository.save(new TicketWaitlist(user, event, ticketType)));
    }

    @Transactional
    public TicketWaitlistResponse cancel(Long waitlistId, Long userId) {
        TicketWaitlist entry = waitlistRepository.findById(waitlistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Waitlist entry not found: " + waitlistId));
        if (!entry.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Waitlist entry not found: " + waitlistId);
        }
        if (entry.getStatus() != WaitlistStatus.WAITING) {
            throw new BusinessException("Waitlist entry is not active: " + entry.getStatus());
        }
        entry.setStatus(WaitlistStatus.CANCELLED);
        return toResponse(waitlistRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public Page<TicketWaitlistResponse> getByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return waitlistRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    private TicketWaitlistResponse toResponse(TicketWaitlist entry) {
        return TicketWaitlistResponse.builder()
                .id(entry.getId())
                .userId(entry.getUser().getId())
                .eventId(entry.getEvent().getId())
                .eventName(entry.getEvent().getEventName())
                .ticketTypeId(entry.getTicketType().getId())
                .ticketTypeName(entry.getTicketType().getTypeName())
                .status(entry.getStatus())
                .createdAt(entry.getCreatedAt())
                .build();
    }
}
