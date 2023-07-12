package gwkim.smartorder.order.service;

import gwkim.smartorder.order.domain.Order;
import gwkim.smartorder.order.response.OrderCommonResponse;

import java.util.List;

public interface OrderService {
    OrderCommonResponse<Order> order(Long cartId);
    List<Order> findAllOrder(Long memberId);
}
