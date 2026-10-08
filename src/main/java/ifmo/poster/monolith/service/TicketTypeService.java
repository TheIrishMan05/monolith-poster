package ifmo.poster.monolith.service;

import ifmo.poster.monolith.dto.request.ticket.CreateTicketTypeRequest;
import ifmo.poster.monolith.dto.request.ticket.UpdateTicketTypeRequest;
import ifmo.poster.monolith.dto.response.ticket.TicketTypeResponse;
import ifmo.poster.monolith.entity.TicketType;
import ifmo.poster.monolith.exception.BusinessException;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.repository.TicketTypeRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final TicketService ticketService;

    public TicketTypeService(
            TicketTypeRepository ticketTypeRepository,
            @Lazy TicketService ticketService
    ) {
        this.ticketTypeRepository = ticketTypeRepository;
        this.ticketService = ticketService;
    }

    @Transactional
    public TicketTypeResponse create(CreateTicketTypeRequest request) {
        if (ticketTypeRepository.existsByTypeNameIgnoreCase(request.getTypeName())) {
            throw new BusinessException("Ticket type already exists: " + request.getTypeName());
        }
        TicketType type = new TicketType();
        type.setTypeName(request.getTypeName().trim());
        type.setPrice(request.getPrice());
        return toResponse(ticketTypeRepository.save(type));
    }

    @Transactional(readOnly = true)
    public TicketTypeResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<TicketTypeResponse> getAll(Pageable pageable) {
        return ticketTypeRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public TicketTypeResponse update(Long id, UpdateTicketTypeRequest request) {
        TicketType type = getEntity(id);

        if (request.getTypeName() != null && !request.getTypeName().isBlank()) {
            String name = request.getTypeName().trim();
            ticketTypeRepository.findByTypeNameIgnoreCase(name)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new BusinessException("Ticket type already exists: " + name);
                    });
            type.setTypeName(name);
        }
        if (request.getPrice() != null) {
            type.setPrice(request.getPrice());
        }
        return toResponse(ticketTypeRepository.save(type));
    }

    @Transactional
    public void delete(Long id) {
        if (!ticketTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket type not found: " + id);
        }
        long tickets = ticketService.countByTicketTypeId(id);
        if (tickets > 0) {
            throw new BusinessException(
                    "Cannot delete ticket type with existing tickets: " + tickets);
        }
        ticketTypeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TicketType getEntity(Long id) {
        return ticketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket type not found: " + id));
    }

    private TicketTypeResponse toResponse(TicketType type) {
        return TicketTypeResponse.builder()
                .id(type.getId())
                .typeName(type.getTypeName())
                .price(type.getPrice())
                .build();
    }
}
