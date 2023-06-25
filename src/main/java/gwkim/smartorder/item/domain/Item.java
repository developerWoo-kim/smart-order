package gwkim.smartorder.item.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gwkim.smartorder.common.auditing.BaseEntity;
import gwkim.smartorder.option.domain.Option;
import gwkim.smartorder.store.domain.Store;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Item extends BaseEntity {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @JsonIgnore
    @OneToMany(mappedBy = "item")
    private List<Option> options = new ArrayList<>();

    private String itemName;
    private int price;
    private String ratingStatus; // P : 인기, R  : 추천
    private String category;
    private String status;
    private long fileId;

    @Builder
    public Item(Store store, String itemName, int price, String ratingStatus, String category, String status) {
        this.store = store;
        this.itemName = itemName;
        this.price = price;
        this.ratingStatus = ratingStatus;
        this.category = category;
        this.status = status;
    }
}
