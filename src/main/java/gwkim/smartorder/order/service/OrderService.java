package gwkim.smartorder.order.service;

import gwkim.smartorder.order.controller.dto.OrderDto;
import gwkim.smartorder.order.domain.Order;
import gwkim.smartorder.order.response.OrderCommonResponse;

import java.util.List;

public interface OrderService {
    OrderCommonResponse<Order> order(Long cartId);

    /**
     * 주문 내역 조회 V1
     * @param memberId
     * @return
     */
    List<Order> findAllOrder(Long memberId);

    /**
     * 주문 내역 조회 V2
     * @param memberId
     * @return
     */
    OrderCommonResponse<List<OrderDto>> findAllOrderV2(Long memberId);
}
