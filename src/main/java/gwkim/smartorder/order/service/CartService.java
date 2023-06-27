package gwkim.smartorder.order.service;

import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.order.domain.Cart;
import gwkim.smartorder.order.response.CartCommonResponse;

/**
 * 장바구니 서비스 인터페이스
 *
 * @author gwkim
 */
public interface CartService {

    CartCommonResponse<Cart> addCartItem(Long memberId, Long itemId, int count, OptionDetail... optionDetails);
}
