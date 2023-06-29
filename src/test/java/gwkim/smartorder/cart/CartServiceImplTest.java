package gwkim.smartorder.cart;

import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.item.repository.ItemRepository;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.option.repository.OptionDetailRepository;
import gwkim.smartorder.order.domain.Cart;
import gwkim.smartorder.order.domain.CartItem;
import gwkim.smartorder.order.repository.CartRepository;
import gwkim.smartorder.order.response.CartCommonResponse;
import gwkim.smartorder.order.service.CartService;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class CartServiceImplTest {
    @Autowired
    CartService cartService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OptionDetailRepository optionDetailRepository;

    @Test
    @DisplayName("장바구니 첫 생성 테스트")
    public void 장바구니_첫_생성_테스트() throws Exception {
        //given
        Long memberId = 1L;
        Long itemId = 1L;
        int itemCount = 1;
        //when
        CartCommonResponse<Cart> response = cartService.addCartItem(memberId, itemId, itemCount);
        //then
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    @DisplayName("상품_아이디가_없을때_테스트")
    public void 상품_아이디가_없을때_테스트() throws Exception {
        //given
        Long memberId = 1L;
        Long itemId = null;
        int itemCount = 1;
        //when
        CartCommonResponse<Cart> response = cartService.addCartItem(memberId, itemId, itemCount);
        //then
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    @DisplayName("옵션이 없는 동일한 상품 주문 테스트")
    public void 옵션이_없는_동일한_상품_주문_테스트() throws Exception {
        //given
        Long memberId = 1L;
        Long itemId = 1L;
        int itemCount = 1;
        //when
        CartCommonResponse<Cart> response;
        cartService.addCartItem(memberId, itemId, itemCount);
        response = cartService.addCartItem(memberId, itemId, itemCount);

        Optional<Cart> cart = cartRepository.findCart(memberId);
        Cart findCart = cart.get();
        CartItem item = findCart.getCartItemList().get(0);

        //then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(cart.isPresent()).isTrue();
        assertThat(item.getCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("다른 매장의 상품을 담을 경우 테스트")
    public void 다른_매장의_상품을_담을_경우_테스트() throws Exception {
        //given
        Long memberId = 1L;
        Long itemId = 1L;
        int itemCount = 1;

        Long memberId2 = 1L;
        Long itemId2 = 4L;
        int itemCount2 = 1;
        //when

        // 1번 매장
        cartService.addCartItem(memberId, itemId, itemCount);
        // 2번 매장
        CartCommonResponse<Cart> response = cartService.addCartItem(memberId2, itemId2, itemCount2);

        Optional<Cart> cart = cartRepository.findCart(memberId);
        Cart findCart = cart.get();
        CartItem item = findCart.getCartItemList().get(0);

        //then
        assertThat(response.getStatus()).isEqualTo("fail");
        assertThat(cart.isPresent()).isTrue();
        assertThat(item.getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("동일한 상품, 옵션을 담을 경우 테스트")
    public void 동일한_상품과옵션을_담을_경우_테스트() throws Exception {
        //given
        Long memberId = 1L;
        Long itemId = 1L;
        int itemCount = 2;

        List<OptionDetail> options = new ArrayList<>();
        options.add(optionDetailRepository.findById(7L).get());
        options.add(optionDetailRepository.findById(11L).get());
        //when

        // 1번 매장
        cartService.addCartItem(memberId, itemId, itemCount, options.toArray(new OptionDetail[options.size()]));

        CartCommonResponse<Cart> response =
                cartService.addCartItem(memberId, itemId, itemCount, options.toArray(new OptionDetail[options.size()]));

        Optional<Cart> cart = cartRepository.findCart(memberId);
        Cart findCart = cart.get();
        CartItem item = findCart.getCartItemList().get(0);

        //then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(cart.isPresent()).isTrue();
        assertThat(item.getCount()).isEqualTo(4);
        assertThat(findCart.getCartItemList().get(0).getCartItemPrice())
                .isEqualTo(itemPriceCalculate(itemId, 4, options.toArray(new OptionDetail[options.size()])));
    }

    @Test
    @DisplayName("상품은 같지만 옵션이 다른 경우 테스트")
    public void 상품은_같지만_옵션이_다른_경우_테스트() throws Exception {
        //given
        Long memberId = 1L;
        Long itemId = 1L;
        int itemCount = 1;

        // 옵션1
        List<OptionDetail> options1 = new ArrayList<>();
        options1.add(optionDetailRepository.findById(7L).get());
        options1.add(optionDetailRepository.findById(11L).get());

        // 옵션2
        List<OptionDetail> options2 = new ArrayList<>();
        options2.add(optionDetailRepository.findById(7L).get());
        //when

        // 1번 매장
        cartService.addCartItem(memberId, itemId, itemCount, options1.toArray(new OptionDetail[options1.size()]));

        CartCommonResponse<Cart> response =
                cartService.addCartItem(memberId, itemId, itemCount, options2.toArray(new OptionDetail[options2.size()]));

        Optional<Cart> cart = cartRepository.findCart(memberId);
        Cart findCart = cart.get();
        CartItem item = findCart.getCartItemList().get(1);

        //then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(cart.isPresent()).isTrue();
        assertThat(item.getCount()).isEqualTo(1);
        assertThat(findCart.getCartItemList().get(1).getCartItemPrice())
                .isEqualTo(itemPriceCalculate(itemId, 1, options2.toArray(new OptionDetail[options2.size()])));
    }

    /**
     * 상품의 옵션과 수량을 계산해주는 편의 메소드
     * @param itemId
     * @param count
     * @param optionDetails
     * @return
     */
    public int itemPriceCalculate(Long itemId, int count, OptionDetail... optionDetails) {
        int totalPrice = 0;
        int itemPrice = 0;
        int totalOptionPrice = 0;

        for(OptionDetail optionDetail : optionDetails) {
            totalOptionPrice += optionDetail.getPrice();
        }

        Item item = itemRepository.findById(itemId).get();
        itemPrice = item.getPrice();

        totalPrice = (itemPrice + totalOptionPrice) * count;

        return totalPrice;
    }
}
