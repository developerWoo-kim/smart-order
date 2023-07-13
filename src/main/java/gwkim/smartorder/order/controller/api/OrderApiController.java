package gwkim.smartorder.order.controller.api;

import gwkim.smartorder.order.controller.dto.OrderDto;
import gwkim.smartorder.order.domain.Order;
import gwkim.smartorder.order.domain.OrderItem;
import gwkim.smartorder.order.repository.OrderRepository;
import gwkim.smartorder.order.response.OrderCommonResponse;
import gwkim.smartorder.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @GetMapping("/api/v1/orders/{memberId}")
    public List<Order> findAllOrder(@PathVariable("memberId") Long memberId) {
        List<Order> allOrder = orderService.findAllOrder(memberId);
        return allOrder;
    }

    @GetMapping("/api/v2/orders/{memberId}")
    public OrderCommonResponse<List<OrderDto>> findAllOrderV2(@PathVariable("memberId") Long memberId) {
        return orderService.findAllOrderV2(memberId);
    }
}
