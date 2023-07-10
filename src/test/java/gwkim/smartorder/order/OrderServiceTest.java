package gwkim.smartorder.order;

import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.item.repository.ItemRepository;
import gwkim.smartorder.member.domain.Member;
import gwkim.smartorder.member.repository.MemberRepository;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.option.repository.OptionDetailRepository;
import gwkim.smartorder.order.domain.Cart;
import gwkim.smartorder.order.domain.CartItem;
import gwkim.smartorder.order.domain.Order;
import gwkim.smartorder.order.domain.OrderItem;
import gwkim.smartorder.order.domain.option.CartOption;
import gwkim.smartorder.order.repository.CartRepository;
import gwkim.smartorder.order.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OrderServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OptionDetailRepository optionDetailRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("장바구니에서 주문상품으로 넘어가는 테스트")
    @Rollback(value = false)
    public void 장바구니에서_주문상품으로_넘어가는_테스트() throws Exception {
        Member member = memberRepository.findById(1L).get();
        Cart cart = cartRepository.findCart(1L).get();
        
        List<OrderItem> orderItemList = new ArrayList<>();
        for(CartItem cartItem : cart.getCartItemList()) {
            List<OptionDetail> optionDetailList = cartItem.getCartOptions().stream()
                    .map(CartOption::getOptionDetail)
                    .collect(Collectors.toList());

            orderItemList.add(OrderItem.createOrderItem(cartItem.getItem(), cartItem.getCount(),
                    optionDetailList.toArray(new OptionDetail[optionDetailList.size()])));
        }

        Order order = Order.createOrder(member, orderItemList.toArray(new OrderItem[orderItemList.size()]));

        orderRepository.save(order);

        assertThat(order.getOrderItemList().size()).isEqualTo(1);
        assertThat(order.getOrderItemList().get(0).getOrderOptions().size()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("주문상품 생성 테스트")
    public void 주문상품_생성_테스트() throws Exception {
        //given
        Long memberId = 1L;
        Long itemId = 1L;
        int itemCount = 2;

        // 옵션1
        List<OptionDetail> options1 = new ArrayList<>();
        options1.add(optionDetailRepository.findById(3L).get());
        options1.add(optionDetailRepository.findById(5L).get());
        
        // 상품
        Item item = itemRepository.findById(itemId).get();
        //when
        // 1번 매장
        OrderItem orderItem = OrderItem.createOrderItem(item, itemCount, options1.toArray(new OptionDetail[options1.size()]));
        //then
        Assertions.assertThat(orderItem.getOrderPrice()).isEqualTo(9000);
    }

    @Test
    @DisplayName("주문 생성 테스트")
    public void 주문_생성_테스트() throws Exception {
        //given
        Long memberId = 1L;
        Long itemId = 1L;
        int itemCount = 2;

        // 옵션1
        List<OptionDetail> options1 = new ArrayList<>();
        options1.add(optionDetailRepository.findById(3L).get());
        options1.add(optionDetailRepository.findById(5L).get());

        // 회원
        Member member = memberRepository.findById(1L).get();
        // 상품
        Item item = itemRepository.findById(itemId).get();
        //when
        // 1번 매장
        OrderItem orderItem = OrderItem.createOrderItem(item, itemCount, options1.toArray(new OptionDetail[options1.size()]));

        Order saveOrder = orderRepository.save(Order.createOrder(member, orderItem));
        //then
        Assertions.assertThat(orderItem.getOrderPrice()).isEqualTo(9000);
        Assertions.assertThat(saveOrder.getOrderItemList().get(0).getId()).isEqualTo(item.getId());
    }

    
}
