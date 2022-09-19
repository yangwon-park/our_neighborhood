package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.store.Store;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MenuDTO {

    @Data
    @NoArgsConstructor
    public static class Add {

        @NotBlank
        private String name;

        @NotNull
        private Integer price;

        @NotNull
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
