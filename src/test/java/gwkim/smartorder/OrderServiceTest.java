package gwkim.smartorder;

import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.item.repository.ItemRepository;
import gwkim.smartorder.member.domain.Member;
import gwkim.smartorder.member.repository.MemberRepository;
import gwkim.smartorder.order.repository.CartRepository;
import gwkim.smartorder.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OrderServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CartRepository cartRepository;

    @Test
    @DisplayName("장바구니가 있을 시 장바구니 생성 테스트")
    public void orderTest() throws Exception {
        //given
        // 회원
        Member member = memberRepository.findById(1L).get();
        // 제품 선택
        Item item = itemRepository.findById(1L).get();
        // 장바구니 담기
            // 기존 장바구니가 존재하는지 확인

                // 없다면 생성
                // 장바구니가 있고 같은 매장이면

        //when
        //then
    }
}
