package gwkim.smartorder.order.controller.dto;

import gwkim.smartorder.option.domain.OptionDetail;
import lombok.Data;

@Data
public class OrderOptionDetailDto {
    private Long optionDetailId;
    private String optionName;
    private String optionDetailName;
    private int optionPrice;

    public OrderOptionDetailDto(OptionDetail optionDetail) {
        optionDetailId = optionDetail.getId();
        optionName = optionDetail.getOption().getName();
        optionDetailName = optionDetail.getName();
        optionPrice = optionDetail.getPrice();
    }
}
