package gwkim.smartorder.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.order.domain.option.CartOption;
import gwkim.smartorder.order.domain.option.OrderOption;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class OrderItem {
    @Id
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private List<OrderOption> orderOptions = new ArrayList<>();

    private int orderPrice;
    private int count;

    //== 생성 메서드 ==//
    @Builder
    public OrderItem(Item item, int orderPrice, int count) {
        this.item = item;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    /**
     * 옵션 생성 편의 메소드
     * @param orderOption
     */
    public void addOrderOptions(OrderOption orderOption) {
        this.orderOptions.add(orderOption);
        orderOption.addOrderItem(this);
    }

    /**
     * 옵션의 가격을 더하는 편의 메소드
     * @param optionPrice
     */
    public void addOptionPrice(int optionPrice) {
        this.orderPrice += optionPrice;
    }

    /**
     * 선택한 갯수만큼 가격 책정
     */
    public void culOrderItemPrice() {
        this.orderPrice *= count;
    }

    /**
     * 주문 상품 생성
     * @param item
     * @param count
     * @param optionDetails
     * @return
     */
    public static OrderItem createOrderItem(Item item, int count, OptionDetail... optionDetails) {
        OrderItem orderItem = new OrderItem().builder()
                .item(item)
                .orderPrice(item.getPrice())
                .count(count)
                .build();

        for (OptionDetail optionDetail : optionDetails) {
            OrderOption orderOption = OrderOption.createOrderOption(optionDetail);
            orderItem.addOrderOptions(orderOption);
            orderItem.addOptionPrice(optionDetail.getPrice());
        }

        orderItem.culOrderItemPrice();
        return orderItem;
    }

    public List<OrderItem> cartItemToOrderItem(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cil -> new OrderItem().builder()
                        .item(cil.getItem())
                        .count(cil.getCount())
                        .orderPrice(cil.getCartItemPrice())
                        .build())
                .collect(Collectors.toList());
    }


}
