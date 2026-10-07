package ifmo.poster.monolith.repository;

import ifmo.poster.monolith.entity.TicketType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    boolean existsByTypeNameIgnoreCase(String typeName);
    Optional<TicketType> findByTypeNameIgnoreCase(String typeName);
}