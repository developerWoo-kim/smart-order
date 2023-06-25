package gwkim.smartorder.order.domain.option;


import gwkim.smartorder.option.domain.OptionDetail;
import gwkim.smartorder.order.domain.CartItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor
public class OrderOption {

    @Id
    @Column(name = "order_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_item_id")
    private CartItem cartItem;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "option_detail_id")
    private OptionDetail optionDetail;
}
