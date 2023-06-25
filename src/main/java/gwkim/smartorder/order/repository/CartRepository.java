package gwkim.smartorder.order.repository;

import gwkim.smartorder.order.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long>, CartCustomRepository{
}
