package gwkim.smartorder.order.controller.dto;

import gwkim.smartorder.order.domain.Order;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderDto {
    private Long orderId;
    private List<OrderItemDto> orderItems;
    private int totalPrice;
    private String status;
    private LocalDateTime orderDate;

    public OrderDto(Order order) {
        orderId = order.getId();
        orderItems = order.getOrderItemList().stream()
                .map(orderItem -> new OrderItemDto(orderItem))
                .collect(Collectors.toList());
        totalPrice = orderItems.stream()
                .mapToInt(oi -> oi.getOrderPrice())
                .sum();
        status = order.getStatus();
        orderDate = order.getOrderDate();
    }
}
