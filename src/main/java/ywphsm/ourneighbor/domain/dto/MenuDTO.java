package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.store.Store;

public class MenuDTO {

    @Data
    @NoArgsConstructor
    public static class Add {
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
}
