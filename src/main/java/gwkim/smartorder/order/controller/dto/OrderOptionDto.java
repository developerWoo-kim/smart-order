package gwkim.smartorder.order.controller.dto;

import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.order.domain.option.OrderOption;
import lombok.Data;

@Data
public class OrderOptionDto {
    private Long orderOptionId;
    private OrderOptionDetailDto orderOptionDetail;
    public OrderOptionDto(OrderOption orderOption) {
        orderOptionId = orderOption.getId();
        orderOptionDetail = new OrderOptionDetailDto(orderOption.getOptionDetail());
    }
}
