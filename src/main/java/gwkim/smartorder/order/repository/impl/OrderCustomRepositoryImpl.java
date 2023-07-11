package gwkim.smartorder.order.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gwkim.smartorder.member.domain.QMember;
import gwkim.smartorder.order.domain.Order;
import gwkim.smartorder.order.domain.QOrder;
import gwkim.smartorder.order.domain.QOrderItem;
import gwkim.smartorder.order.repository.OrderCustomRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static gwkim.smartorder.member.domain.QMember.*;
import static gwkim.smartorder.order.domain.QOrder.*;
import static gwkim.smartorder.order.domain.QOrderItem.*;

@RequiredArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<Order> findAllOrder(Long memberId) {
        return jpaQueryFactory
                .selectFrom(order)
                .join(order.member, member)
                .fetchJoin()
                .leftJoin(order.orderItemList, orderItem)
                .fetch();
    }
}
