package gwkim.smartorder.order.service;

import gwkim.smartorder.order.domain.Order;
import gwkim.smartorder.order.response.OrderCommonResponse;

public interface OrderService {
    OrderCommonResponse<Order> order(Long cartId);
}
