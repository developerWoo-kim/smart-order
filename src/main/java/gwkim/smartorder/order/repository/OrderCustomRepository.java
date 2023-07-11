package gwkim.smartorder.order.repository;

import gwkim.smartorder.order.domain.Order;

import java.util.List;

public interface OrderCustomRepository {
    List<Order> findAllOrder(Long memberId);
}
