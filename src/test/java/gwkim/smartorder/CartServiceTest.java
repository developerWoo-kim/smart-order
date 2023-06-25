package gwkim.smartorder;

import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.item.repository.ItemRepository;
import gwkim.smartorder.member.domain.Member;
import gwkim.smartorder.member.repository.MemberRepository;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.option.repository.OptionDetailRepository;
import gwkim.smartorder.order.domain.Cart;
import gwkim.smartorder.order.domain.CartItem;
import gwkim.smartorder.order.domain.option.CartOption;
import gwkim.smartorder.order.repository.CartRepository;
import gwkim.smartorder.order.repository.OrderRepository;
import gwkim.smartorder.store.domain.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional(readOnly = true)
public class CartServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OptionDetailRepository optionDetailRepository;

    @Test
    @DisplayName("장바구니 연관관계 테스트")
    @Transactional(readOnly = false)
    @Rollback(value = false)
    public void cartRelationTest() throws Exception {
        //given
        // 회원
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item item = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
//        optionDetailList.add(optionDetailRepository.findById(7L).get());
//        optionDetailList.add(optionDetailRepository.findById(11L).get());

        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(item, 2, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니 생성
        Cart cart = cartRepository.findCart(member.getId())
                .orElseGet(() -> Cart.createCart(member, cartItem));
        if(cart != null) {
            Store store = cart.getCartItemList().stream()
                    .map(CartItem::getItem)
                    .map(Item::getStore)
                    .findFirst()
                    .orElse(null);

        }


        Cart saveCart = cartRepository.save(cart);
        //when
        Cart findCart = cartRepository.findById(saveCart.getId()).get();
        //then
        assertThat(findCart).isNotNull();
        assertThat(findCart.getMember()).isNotNull();
        assertThat(findCart.getCartItemList()).isNotNull();
    }

    @Test
    @DisplayName("주문 상품의 옵션에 따른 가격 검증 테스트")
    @Rollback(value = false)
    public void optionPriceVerification() throws Exception {
        //given
        int itemCount = 2;
        int itemPrice = 0;
        int optionPrice = 0;
        int totalPrice = 0;
        // 회원
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item item = itemRepository.findById(2L).get();
        itemPrice = item.getPrice();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        OptionDetail optionDetail1 = optionDetailRepository.findById(7L).get();
        optionPrice += optionDetail1.getPrice();
        OptionDetail optionDetail2 = optionDetailRepository.findById(11L).get();
        optionPrice += optionDetail2.getPrice();
        optionDetailList.add(optionDetail1);
        optionDetailList.add(optionDetail2);
        //when
        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(item, itemCount, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니 생성
        Cart cart = Cart.createCart(member, cartItem);

        Cart saveCart = cartRepository.save(cart);
        //when
        Cart findCart = cartRepository.findById(saveCart.getId()).get();
        //then
        totalPrice = (itemPrice + optionPrice) * itemCount;
        assertThat(findCart.getCartItemList().get(0).getCartItemPrice()).isEqualTo(totalPrice);
    }

    @Test
    @DisplayName("동일한 옵션의 상품을 선택했을 경우의 검증 테스트")
    public void sameOptionVerification() throws Exception {
        //given
        // 회원 선택
        Member member = memberRepository.findById(1L).get();
        // 제품 선택´
        Item item = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        optionDetailList.add(optionDetailRepository.findById(7L).get());
        optionDetailList.add(optionDetailRepository.findById(11L).get());
        // 상품 생성
        CartItem cartItem = CartItem.createCartItem(item, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));
        // 장바구니 생성
        Cart cart = Cart.createCart(member, cartItem);
        Cart saveCart = cartRepository.save(cart);
        //when
        // 회원 선택
        Member member2 = memberRepository.findById(1L).get();
        // 제품 선택´
        Item item2 = itemRepository.findById(2L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList2 = new ArrayList<>();
        optionDetailList2.add(optionDetailRepository.findById(7L).get());
        optionDetailList2.add(optionDetailRepository.findById(11L).get());
        // 상품 생성
        // 장바구니에 상품중에 같은 상품이 있는지 확인

        // -> 같은 상품 아이디가 없는 경우 장바구니 상품 생성
        //   -> create
        // -> 같은 상품 아이디가 있는 경우 옵션 확인
        //   -> 같은 옵션이 있는 경우 수량만 추가 (변경감지 사용)
        //   -> 같은 옵션이 아닌 경우 장바구니 상품 생성


//        CartItem cartItem = CartItem.createCartItem(item, 1, optionDetailList.toArray(new OptionDetail[optionDetailList.size()]));


        //then
    }

    @Test
    @DisplayName("생성된 장바구니가 있는지 테스트")
    public void cartFindTest() throws Exception {
        //given
        Item findItem = itemRepository.findById(2L).get();
        // 회원 선택
        Member member = memberRepository.findById(1L).get();
        // 옵션 생성
        List<OptionDetail> optionDetailList = new ArrayList<>();
        optionDetailList.add(optionDetailRepository.findById(7L).get());
        optionDetailList.add(optionDetailRepository.findById(12L).get());

        System.out.println("========= CART 조회 시작 =============");
        Optional<Cart> findCart = cartRepository.findCart(1L);
//        Cart cart = findCart.get();
        System.out.println("========= CART 조회 끝 =============");
//        System.out.println(findCart.get().getCartItemList().size());

        boolean allMatch = false;
        if(findCart.isPresent()) {
            List<Long> cartOptionIds = findCart.get().getCartItemList().stream()
                    .flatMap(cartItem -> Optional.ofNullable(cartItem.getCartOptions()).orElse(Collections.emptyList()).stream())
                    .map(cartOption -> cartOption.getOptionDetail().getId())
                    .sorted()
                    .collect(Collectors.toList());

            // 옵션이 없는 상품만 있다면 장바구니에 바로 담기
            if(cartOptionIds.isEmpty()) {
                assertThat(allMatch).isFalse();
            } else {
                List<Long> sortedOptionDetailIds = optionDetailList.stream()
                        .map(OptionDetail::getId)
                        .sorted()
                        .collect(Collectors.toList());

                allMatch = cartOptionIds.equals(sortedOptionDetailIds);
                assertThat(allMatch).isTrue();
            }
        }

        //when
        //then
    }
}
