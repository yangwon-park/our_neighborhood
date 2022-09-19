package ywphsm.ourneighbor.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.store.Store;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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

        public Add(Menu menu) {
            this.name = menu.getName();
            this.price = menu.getPrice();
        }

        @Builder
        public Menu toEntity(Store store) {
            return Menu.builder()
                    .name(name)
                    .price(price)
                    .store(store)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class Detail {

        @NotBlank
        private String name;

        @NotNull
        private Integer price;

        @NotNull
        private int discountPrice;

        private String storedFileName;

        private LocalDateTime discountStart;

        private LocalDateTime discountEnd;

        public Detail(Menu menu) {
            this.name = menu.getName();
            this.price = menu.getPrice();
            this.discountPrice = menu.getDiscountPrice();
            this.storedFileName = menu.getFile().getStoredFileName();
            this.discountStart = menu.getDiscountStart();
            this.discountEnd = menu.getDiscountEnd();
        }
    }

}
