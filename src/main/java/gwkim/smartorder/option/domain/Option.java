package gwkim.smartorder.option.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gwkim.smartorder.common.auditing.BaseEntity;
import gwkim.smartorder.item.domain.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Option extends BaseEntity {
    @Id
    @Column(name = "option_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL)
    private List<OptionDetail> optionDetailList = new ArrayList<>();

    private String name;
    private String required;
    private int minSelectCount;
    private int maxSelectCount;
    private int orders;

    @Builder
    public Option(Item item, String name, String required, int minSelectCount, int maxSelectCount, int orders) {
        this.item = item;
        this.name = name;
        this.required = required;
        this.minSelectCount = minSelectCount;
        this.maxSelectCount = maxSelectCount;
        this.orders = orders;
    }

    /**
     * 옵션 생성 메서드 (상세 정보 포함)
     *
     * @param item
     * @param name
     * @param required
     * @param minSelectCount
     * @param maxSelectCount
     * @param orders
     * @param optionDetails
     * @return
     */
    public static Option createOption(Item item, String name, String required, int minSelectCount,
                                      int maxSelectCount, int orders, OptionDetail... optionDetails) {

        Option option = new Option().builder()
                .item(item)
                .name(name)
                .required(required)
                .minSelectCount(minSelectCount)
                .maxSelectCount(maxSelectCount)
                .orders(orders)
                .build();

        for(OptionDetail optionDetail : optionDetails) {
            option.addOptionDetail(optionDetail);
        }

        return option;
    }

    /**
     * 옵션 상세정보 세팅 편의 메소드
     *
     * @param optionDetail
     */
    public void addOptionDetail(OptionDetail optionDetail) {
        optionDetailList.add(optionDetail);
        optionDetail.setOption(this);
    }
}
