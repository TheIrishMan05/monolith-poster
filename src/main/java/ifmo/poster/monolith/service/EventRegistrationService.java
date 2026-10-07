package ifmo.poster.monolith.service;

import ifmo.poster.monolith.dto.request.event.CreateEventRegistrationRequest;
import ifmo.poster.monolith.dto.response.common.EventBriefDto;
import ifmo.poster.monolith.dto.response.event.EventRegistrationResponse;
import ifmo.poster.monolith.dto.response.user.UserResponse;
import ifmo.poster.monolith.entity.Event;
import ifmo.poster.monolith.entity.EventRegistration;
import ifmo.poster.monolith.entity.User;
import ifmo.poster.monolith.enums.EventStatus;
import ifmo.poster.monolith.enums.RegistrationStatus;
import ifmo.poster.monolith.exception.BusinessException;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.repository.EventRegistrationRepository;
import ifmo.poster.monolith.repository.EventRepository;
import ifmo.poster.monolith.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public EventRegistrationResponse register(Long userId, CreateEventRegistrationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found: " + request.getEventId()));

        if (event.getStatus() != EventStatus.ACTIVE) {
            throw new BusinessException("Event is not open for registration: " + event.getId());
        }

        Optional<EventRegistration> existing =
                registrationRepository.findByUserIdAndEventId(userId, event.getId());

        if (existing.isPresent()) {
            EventRegistration registration = existing.get();
            if (registration.getStatus() != RegistrationStatus.CANCELED) {
                throw new BusinessException(
                        "User already registered for event: " + event.getId());
            }
            // повторная регистрация после отмены (unique user+event уже есть в БД)
            registration.setStatus(RegistrationStatus.CONFIRMED);
            registration.setRegistrationDate(LocalDateTime.now());
            return toResponse(registrationRepository.save(registration));
        }

        EventRegistration created = new EventRegistration(user, event, RegistrationStatus.CONFIRMED);
        return toResponse(registrationRepository.save(created));
    }

    @Transactional
    public EventRegistrationResponse cancel(Long registrationId) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found: " + registrationId));

        if (registration.getStatus() == RegistrationStatus.CANCELED) {
            throw new BusinessException("Registration already canceled");
        }

        registration.setStatus(RegistrationStatus.CANCELED);
        return toResponse(registrationRepository.save(registration));
    }

    @Transactional(readOnly = true)
    public EventRegistrationResponse getById(Long id) {
        return toResponse(registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found: " + id)));
    }

    @Transactional(readOnly = true)
    public Page<EventRegistrationResponse> getByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return registrationRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EventRegistrationResponse> getByEvent(Long eventId, Pageable pageable) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found: " + eventId);
        }
        return registrationRepository.findByEventId(eventId, pageable).map(this::toResponse);
    }

    private EventRegistrationResponse toResponse(EventRegistration registration) {
        Event event = registration.getEvent();
        User user = registration.getUser();

        return EventRegistrationResponse.builder()
                .id(registration.getId())
                .registrationDate(registration.getRegistrationDate())
                .status(registration.getStatus())
                .event(EventBriefDto.builder()
                        .id(event.getId())
                        .eventName(event.getEventName())
                        .dateTime(event.getDateTime())
                        .location(event.getLocation())
                        .build())
                .user(UserResponse.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .build();
    }
}