package ifmo.poster.monolith.service;

import ifmo.poster.monolith.entity.Seat;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public Seat getEntity(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + id));
    }

    @Transactional
    public Seat save(Seat seat) {
        return seatRepository.save(seat);
    }

    @Transactional
    public void delete(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Seat not found: " + id);
        }
        seatRepository.deleteById(id);
    }
}
