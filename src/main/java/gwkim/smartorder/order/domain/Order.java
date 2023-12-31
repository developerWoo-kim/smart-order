package gwkim.smartorder.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gwkim.smartorder.member.domain.Member;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.order.domain.option.OrderOption;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

@Entity(name = "orders")
@Getter @Setter
@NoArgsConstructor
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItemList = new ArrayList<>();

    private String status;
    private LocalDateTime orderDate;
    private Long storeId;

    @Builder
    public Order(String status, LocalDateTime orderDate) {
        this.status = status;
        this.orderDate = orderDate;
    }

    //== 편의 메소드 ==//
    public void addMember(Member member) {
        this.member = member;
    }
    public void addOrderItem(OrderItem orderItem) {
        orderItemList.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * 주문 상품 프록시를 초기화하는 편의 메서드
     */
    public void getOrderItemsWithOptions() {
        List<OrderItem> orderItems = this.getOrderItemList();
        orderItems.stream().forEach(io -> io.getItem().getItemName());
        orderItems.stream()
                .forEach(io -> {
                    List<OrderOption> orderOptions = io.getOrderOptions();
                    orderOptions.stream()
                            .forEach(orderOption -> {
                                orderOption.getOptionDetail().getName();
                                orderOption.getOptionDetail().getOption().getName();
                            });
                });
    }

    //== 생성 메서드 ==//
    /**
     * 주문 생성
     *
     * @param member
     * @param orderItems
     * @return
     */
    public static Order createOrder(Member member, Long storeId, OrderItem... orderItems) {
        Order order = new Order().builder()
                .status("test")
                .orderDate(LocalDateTime.now())
                .build();
        order.addMember(member);
        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.storeId = storeId;
        return order;
    }
}
