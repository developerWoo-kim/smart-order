package gwkim.smartorder.store.controller.dto;

import gwkim.smartorder.store.domain.Store;
import lombok.Data;

@Data
public class StoreDto {
    private Long storeId;
    private String storeName;
    public StoreDto(Store store) {
        storeId = store.getId();
        storeName = store.getStoreName();
    }
}
