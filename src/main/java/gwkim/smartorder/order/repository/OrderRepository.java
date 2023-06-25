package gwkim.smartorder.order.repository;

import gwkim.smartorder.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
