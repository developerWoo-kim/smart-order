package gwkim.smartorder.order.controller.dto;

import gwkim.smartorder.order.domain.OrderItem;
import gwkim.smartorder.order.domain.option.OrderOption;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderItemDto {
    private String itemName;
    private int orderPrice;
    private int count;
    private List<OrderOptionDto> orderOptions;

    public OrderItemDto(OrderItem orderItem) {
        itemName = orderItem.getItem().getItemName();
        orderPrice = orderItem.getOrderPrice();
        count = orderItem.getCount();
        orderOptions = orderItem.getOrderOptions().stream()
                .map(orderOption -> new OrderOptionDto(orderOption))
                .collect(Collectors.toList());
    }
}
