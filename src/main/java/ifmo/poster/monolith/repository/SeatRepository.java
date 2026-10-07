package ifmo.poster.monolith.repository;

import ifmo.poster.monolith.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}