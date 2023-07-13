package gwkim.smartorder.order.service.impl;


import gwkim.smartorder.member.domain.Member;
import gwkim.smartorder.member.repository.MemberRepository;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.order.controller.dto.OrderDto;
import gwkim.smartorder.order.domain.Cart;
import gwkim.smartorder.order.domain.CartItem;
import gwkim.smartorder.order.domain.Order;
import gwkim.smartorder.order.domain.OrderItem;
import gwkim.smartorder.order.domain.option.CartOption;
import gwkim.smartorder.order.domain.option.OrderOption;
import gwkim.smartorder.order.repository.CartRepository;
import gwkim.smartorder.order.repository.OrderRepository;
import gwkim.smartorder.order.response.OrderCommonResponse;
import gwkim.smartorder.order.service.OrderService;
import gwkim.smartorder.store.controller.dto.StoreDto;
import gwkim.smartorder.store.domain.Store;
import gwkim.smartorder.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final StoreRepository storeRepository;
    @Override
    public OrderCommonResponse<Order> order(Long memberId) {
        Optional<Cart> findCart = Optional.ofNullable(cartRepository.findCart(memberId)
                .orElseThrow(IllegalArgumentException::new));
        Cart cart = findCart.get();

        Optional<Member> member = Optional.ofNullable(memberRepository.findById(memberId))
                .orElseThrow(IllegalAccessError::new);
        Member findMember = member.get();

        List<OrderItem> orderItemList = new ArrayList<>();
        for(CartItem cartItem : cart.getCartItemList()) {
            List<OptionDetail> optionDetailList = cartItem.getCartOptions().stream()
                    .map(CartOption::getOptionDetail)
                    .collect(Collectors.toList());

            orderItemList.add(OrderItem.createOrderItem(cartItem.getItem(), cartItem.getCount(),
                    optionDetailList.toArray(new OptionDetail[optionDetailList.size()])));
        }

        Order order = Order.createOrder(findMember, cart.getStoreId(), orderItemList.toArray(new OrderItem[orderItemList.size()]));

        orderRepository.save(order);

        return OrderCommonResponse.orderSuccess(order);
    }
    @Override
    @Transactional
    public List<Order> findAllOrder(Long memberId) {
        List<Order> findOrders = orderRepository.findAllOrder(memberId);
        for (Order order : findOrders) {
//            order.getMember().getName();
            List<OrderItem> orderItems = order.getOrderItemList();
            orderItems.stream()
                    .forEach(io -> io.getItem().getItemName());
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
        return findOrders;
    }

    /**
     * 주문 내역 조회 서비스 V2
     *
     * @param memberId
     * @return
     */
    @Override
    @Transactional
    public OrderCommonResponse<List<OrderDto>> findAllOrderV2(Long memberId) {
        List<Order> findOrders = orderRepository.findAllOrder(memberId);
        for (Order order : findOrders) {
            List<OrderItem> orderItems = order.getOrderItemList();
            orderItems.stream()
                    .forEach(io -> io.getItem().getItemName());
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
        List<OrderDto> orderDto = findOrders.stream()
                .map(m -> {
                    OrderDto dto = new OrderDto(m);
                    Optional<Store> findStore = storeRepository.findById(m.getStoreId());
                    if(findStore.isPresent()){
                        dto.setStore(new StoreDto(findStore.get()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return OrderCommonResponse.commonSuccess(orderDto);
    }
}
