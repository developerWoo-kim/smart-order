package gwkim.smartorder.option.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor
public class OptionDetail {

    @Id
    @Column(name = "option_detail_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "option_id")
    private Option option;

    private String name;
    private int price;
    private int orders;

    @Builder
    public OptionDetail(String name, int price, int orders) {
        this.name = name;
        this.price = price;
        this.orders = orders;
    }
}
