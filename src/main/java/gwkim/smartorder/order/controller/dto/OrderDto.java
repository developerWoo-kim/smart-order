package gwkim.smartorder.order.controller.dto;

import gwkim.smartorder.order.domain.Order;
import gwkim.smartorder.store.controller.dto.StoreDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderDto {
    private Long orderId;
    private List<OrderItemDto> orderItems;
    private int totalPrice;
    private String status;
    private String orderDate;
    private StoreDto store;

    public OrderDto(Order order) {
        orderId = order.getId();
        orderItems = order.getOrderItemList().stream()
                .map(orderItem -> new OrderItemDto(orderItem))
                .collect(Collectors.toList());
        totalPrice = orderItems.stream()
                .mapToInt(oi -> oi.getOrderPrice())
                .sum();
        status = order.getStatus();
        orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("YYYY.MM.dd (E)"));
    }
}
