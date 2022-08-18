package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.store.Store;

@Data
public class MenuAddDTO {

    private String name;
    private Integer price;

    private Long storeId;

    @Builder
    public Menu toEntity(Store store) {
        return Menu.builder()
                .name(name)
                .price(price)
                .store(store)
                .build();
    }

}
