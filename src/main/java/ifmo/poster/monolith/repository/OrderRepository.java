package ifmo.poster.monolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ifmo.poster.monolith.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
  
}
