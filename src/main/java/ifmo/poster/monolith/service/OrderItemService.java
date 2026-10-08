package ifmo.poster.monolith.service;

import ifmo.poster.monolith.entity.OrderItem;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public OrderItem getEntity(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found: " + id));
    }

    @Transactional
    public OrderItem save(OrderItem item) {
        return orderItemRepository.save(item);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order item not found: " + id);
        }
        orderItemRepository.deleteById(id);
    }
}
