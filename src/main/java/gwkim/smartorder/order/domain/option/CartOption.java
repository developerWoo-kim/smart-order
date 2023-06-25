package gwkim.smartorder.order.domain.option;

import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.order.domain.CartItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
public class CartOption {

    @Id
    @Column(name = "cart_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_item_id")
    private CartItem cartItem;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "option_detail_id")
    private OptionDetail optionDetail;

    //== 연관 관계 메서드 ==//
    public void addCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
    //== 생성 메서드 ==//
    public static CartOption createCartOption(OptionDetail optionDetail) {
        CartOption cartOption = new CartOption();
        cartOption.optionDetail = optionDetail;
        return cartOption;
    }
}
