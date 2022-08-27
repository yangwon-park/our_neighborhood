package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.store.Store;

@Data
public class MenuAddDTO {

    private String name;

    private Integer price;

    private Long storeId;

    private MultipartFile image;


    @Builder
    public Menu toEntity(Store store) {
        return Menu.builder()
                .name(name)
                .price(price)
                .store(store)
                .build();
    }

}
