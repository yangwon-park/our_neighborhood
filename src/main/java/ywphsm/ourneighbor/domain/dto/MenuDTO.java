package ywphsm.ourneighbor.domain.dto;

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

        private MultipartFile file;

        @Builder
        public Add(String name, Integer price, Long storeId, MultipartFile file) {
            this.name = name;
            this.price = price;
            this.storeId = storeId;
            this.file = file;
        }

        public Add(Menu menu) {
            this.name = menu.getName();
            this.price = menu.getPrice();
        }

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
    public static class Update {

        @NotNull
        private Long id;

        @NotBlank
        private String name;

        @NotNull
        private Integer price;

        @NotNull
        private Long storeId;

        private String storedFileName;

        private MultipartFile file;

        private int discountPrice;

        private LocalDateTime discountStart;

        private LocalDateTime discountEnd;

        @Builder
        public Update(Long id, String name, Integer price, Long storeId,
                      String storedFileName, MultipartFile file,
                      int discountPrice, LocalDateTime discountStart, LocalDateTime discountEnd) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.storeId = storeId;
            this.storedFileName = storedFileName;
            this.file = file;
            this.discountPrice = discountPrice;
            this.discountStart = discountStart;
            this.discountEnd = discountEnd;
        }

        @Builder
        public Update(Menu menu) {
            this.id = menu.getId();
            this.name = menu.getName();
            this.price = menu.getPrice();
            this.storeId = menu.getStore().getId();
            this.storedFileName = menu.getFile().getStoredFileName();
            this.discountPrice = menu.getDiscountPrice();
            this.discountStart = menu.getDiscountStart();
            this.discountEnd = menu.getDiscountEnd();
        }

        public Menu toEntity() {
            return Menu.builder()
                    .id(id)
                    .name(name)
                    .price(price)
                    .discountPrice(discountPrice)
                    .discountStart(discountStart)
                    .discountEnd(discountEnd)
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
