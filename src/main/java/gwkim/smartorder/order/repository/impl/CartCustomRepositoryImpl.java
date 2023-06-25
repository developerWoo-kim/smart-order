package gwkim.smartorder.order.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gwkim.smartorder.order.domain.Cart;
import gwkim.smartorder.order.domain.QCart;
import gwkim.smartorder.order.repository.CartCustomRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static gwkim.smartorder.order.domain.QCart.*;

@RequiredArgsConstructor
public class CartCustomRepositoryImpl implements CartCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Cart> findCart(Long memberId) {
        Cart findCart = queryFactory
                .selectFrom(cart)
                .where(cart.member.id.eq(memberId))
                .fetchJoin()
                .fetchOne();
        return Optional.ofNullable(findCart);
    }
}
