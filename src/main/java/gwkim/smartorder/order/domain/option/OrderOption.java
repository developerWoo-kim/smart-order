package gwkim.smartorder.order.domain.option;


import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.order.domain.CartItem;
import gwkim.smartorder.order.domain.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor
public class OrderOption {

    @Id
    @Column(name = "order_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "option_detail_id")
    private OptionDetail optionDetail;

    //== 연관 관계 메서드 ==//
    public void addOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
    //== 생성 메서드 ==//
    public static OrderOption createOrderOption(OptionDetail optionDetail) {
        OrderOption orderOption = new OrderOption();
        orderOption.optionDetail = optionDetail;
        return orderOption;
    }
}
