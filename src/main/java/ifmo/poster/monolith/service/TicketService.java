package ifmo.poster.monolith.service;

import ifmo.poster.monolith.dto.request.ticket.CreateTicketInventoryRequest;
import ifmo.poster.monolith.dto.response.ticket.TicketInventoryResponse;
import ifmo.poster.monolith.dto.response.ticket.TicketResponse;
import ifmo.poster.monolith.entity.Event;
import ifmo.poster.monolith.entity.Seat;
import ifmo.poster.monolith.entity.Ticket;
import ifmo.poster.monolith.entity.TicketType;
import ifmo.poster.monolith.enums.EventStatus;
import ifmo.poster.monolith.enums.TicketStatus;
import ifmo.poster.monolith.exception.BusinessException;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.repository.EventRepository;
import ifmo.poster.monolith.repository.TicketRepository;
import ifmo.poster.monolith.repository.TicketTypeRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;

    @Transactional
    public TicketInventoryResponse createInventory(CreateTicketInventoryRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found: " + request.getEventId()));

        if (event.getStatus() != EventStatus.ACTIVE
                && event.getStatus() != EventStatus.PENDING) {
            throw new BusinessException(
                    "Cannot create inventory for event in status: " + event.getStatus());
        }

        TicketType ticketType = ticketTypeRepository.findById(request.getTicketTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket type not found: " + request.getTicketTypeId()));

        boolean hasSeats = request.getSeats() != null && !request.getSeats().isEmpty();
        List<Ticket> created = new ArrayList<>();

        if (hasSeats) {
            validateSeats(request.getSeats());
            if (request.getQuantity() != null
                    && request.getQuantity() != request.getSeats().size()) {
                throw new BusinessException(
                        "quantity must equal seats.size() when seats are provided");
            }

            for (CreateTicketInventoryRequest.SeatSpec spec : request.getSeats()) {
                Ticket ticket = newTicket(event, ticketType);
                ticket.setSeat(buildSeat(spec, ticket));
                created.add(ticketRepository.save(ticket));
            }
        } else {
            if (request.getQuantity() == null) {
                throw new BusinessException(
                        "Provide quantity (without seats) or a non-empty seats list");
            }
            for (int i = 0; i < request.getQuantity(); i++) {
                created.add(ticketRepository.save(newTicket(event, ticketType)));
            }
        }

        long availableTotal = ticketRepository.countByEventIdAndTicketTypeIdAndStatus(
                event.getId(),
                ticketType.getId(),
                TicketStatus.AVAILABLE
        );

        return TicketInventoryResponse.builder()
                .eventId(event.getId())
                .ticketTypeId(ticketType.getId())
                .createdCount(created.size())
                .availableTotal(availableTotal)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> getByEvent(Long eventId, TicketStatus status, Pageable pageable) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found: " + eventId);
        }
        if (status == null) {
            return ticketRepository.findByEventId(eventId, pageable).map(this::toResponse);
        }
        return ticketRepository.findByEventIdAndStatus(eventId, status, pageable)
                .map(this::toResponse);
    }

    private Ticket newTicket(Event event, TicketType ticketType) {
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setTicketType(ticketType);
        ticket.setStatus(TicketStatus.AVAILABLE);
        return ticket;
    }

    private Seat buildSeat(CreateTicketInventoryRequest.SeatSpec spec, Ticket ticket) {
        Seat seat = new Seat();
        seat.setRow(spec.getRow().trim());
        seat.setNumber(spec.getNumber());
        seat.setSectorName(spec.getSectorName());
        seat.setXCoordinate(spec.getXCoordinate());
        seat.setYCoordinate(spec.getYCoordinate());
        seat.setTicket(ticket);
        return seat;
    }

    private void validateSeats(List<CreateTicketInventoryRequest.SeatSpec> seats) {
        Set<String> unique = new HashSet<>();
        for (CreateTicketInventoryRequest.SeatSpec spec : seats) {
            String key = spec.getRow().trim().toUpperCase() + ":" + spec.getNumber();
            if (!unique.add(key)) {
                throw new BusinessException("Duplicate seat in request: " + key);
            }
        }
    }

    private TicketResponse toResponse(Ticket ticket) {
        TicketResponse.SeatInfo seatInfo = null;
        if (ticket.getSeat() != null) {
            Seat seat = ticket.getSeat();
            seatInfo = new TicketResponse.SeatInfo(
                    seat.getRow(),
                    seat.getNumber(),
                    seat.getSectorName(),
                    seat.getXCoordinate(),
                    seat.getYCoordinate()
            );
        }

        return TicketResponse.builder()
                .id(ticket.getId())
                .eventName(ticket.getEvent().getEventName())
                .eventDateTime(ticket.getEvent().getDateTime())
                .ticketTypeName(ticket.getTicketType().getTypeName())
                .status(ticket.getStatus())
                .seat(seatInfo)
                .build();
    }
}