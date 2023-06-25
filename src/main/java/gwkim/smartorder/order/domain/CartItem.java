package gwkim.smartorder.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gwkim.smartorder.item.domain.Item;
import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.order.domain.option.CartOption;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
public class CartItem {
    @Id
    @Column(name = "cart_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL)
    private List<CartOption> cartOptions = new ArrayList<>();

    private int cartItemPrice;
    private int count;

    @Builder
    public CartItem(Item item, int cartItemPrice, int count) {
        this.item = item;
        this.cartItemPrice = cartItemPrice;
        this.count = count;
    }

    //== 연관관계 메서드 ==//
    public void addCart(Cart cart) {
        this.cart = cart;
    }

    public void addCartOptions(CartOption cartOption) {
        this.cartOptions.add(cartOption);
        cartOption.addCartItem(this);
    }

    /**
     * 옵션의 가격을 더하는 편의 메소드
     * @param optionPrice
     */
    public void addOptionPrice(int optionPrice) {
        this.cartItemPrice += optionPrice;
    }

    /**
     * 선택한 갯수만큼 가격 책정
     */
    public void culCartItemPrice() {
        this.cartItemPrice *= count;
    }


    //== 생성 메서드 ==//
    /**
     * 장바구니 상품 생성
     *
     * @param item
     * @param count
     * @return
     */
    public static CartItem createCartItem(Item item, int count, OptionDetail... optionDetails) {
        CartItem cartItem = new CartItem().builder()
                .item(item)
                .cartItemPrice(item.getPrice())
                .count(count)
                .build();

        for (OptionDetail optionDetail : optionDetails) {
            CartOption cartOption = CartOption.createCartOption(optionDetail);
            cartItem.addCartOptions(cartOption);
            cartItem.addOptionPrice(optionDetail.getPrice());
        }

        cartItem.culCartItemPrice();

        return cartItem;
    }

}
