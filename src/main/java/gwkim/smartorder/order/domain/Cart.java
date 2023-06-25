package gwkim.smartorder.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gwkim.smartorder.common.auditing.BaseEntity;
import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.member.domain.Member;
import gwkim.smartorder.option.domain.OptionDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor
public class Cart extends BaseEntity {
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItemList = new ArrayList<>();

    //== 연관 관계 메서드 ==//

    /**
     * 회원 추가
     * @param member
     */
    public void addMember(Member member) {
        this.member = member;
        member.addCart(this);
    }
    /**
     * 상품 추가
     * @param cartItem
     */
    public void addCartItem(CartItem cartItem){
        cartItemList.add(cartItem);
        cartItem.addCart(this);
    }

    //== 생성 메소드 ==//

    /**
     * 장바구니 생성
     *
     * @param member
     * @param cartItem
     * @return
     */
    public static Cart createCart(Member member, CartItem cartItem) {
        Cart cart = new Cart();
        cart.addMember(member);
        cart.addCartItem(cartItem);
        return cart;
    }
}
