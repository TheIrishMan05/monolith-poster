package ifmo.poster.monolith.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ifmo.poster.monolith.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  
}
