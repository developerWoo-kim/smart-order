package gwkim.smartorder.order.repository;

import gwkim.smartorder.order.domain.Cart;

import java.util.Optional;

public interface CartCustomRepository {
    /**
     * 회원 아이디로 장바구니 조회
     * @param memberId
     * @return
     */
    Optional<Cart> findCart(Long memberId);
}
