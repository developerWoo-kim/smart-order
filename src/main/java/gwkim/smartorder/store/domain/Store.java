package gwkim.smartorder.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gwkim.smartorder.common.auditing.BaseEntity;
import gwkim.smartorder.item.domain.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Store extends BaseEntity {
    @Id
    @Column(name = "store_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String storeName;
    private String address;
    private String phone;
    private int minOrderPrice;
    private int minPickupTime;
    private int maxPickupTime;
    private String status; // Y : 운영, N : 종료

    @JsonIgnore
    @OneToMany(mappedBy = "store")
    private List<Item> item = new ArrayList<>();

    @Builder
    public Store(String storeName, String address, String phone, int minOrderPrice, int minPickupTime, int maxPickupTime, String status) {
        this.storeName = storeName;
        this.address = address;
        this.phone = phone;
        this.minOrderPrice = minOrderPrice;
        this.minPickupTime = minPickupTime;
        this.maxPickupTime = maxPickupTime;
        this.status = status;
    }
}
