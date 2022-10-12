package ywphsm.ourneighbor.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.menu.MenuFeat;
import ywphsm.ourneighbor.domain.menu.MenuType;
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

        @NotBlank
        private MenuType type;

        private MenuFeat feature;

        @NotNull
        private Long storeId;

        private MultipartFile file;

        @Builder
        public Add(String name, Integer price,
                   MenuType type, MenuFeat feature,
                   Long storeId, MultipartFile file) {
            this.name = name;
            this.price = price;
            this.type = type;
            this.feature = feature;
            this.storeId = storeId;
            this.file = file;
        }

        public Menu toEntity(Store store) {
            return Menu.builder()
                    .name(name)
                    .price(price)
                    .type(type)
                    .feature(feature)
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

        private String uploadImgUrl;

        private MultipartFile file;

        private int discountPrice;

        private LocalDateTime discountStart;

        private LocalDateTime discountEnd;

        @NotBlank
        private MenuType type;

        private MenuFeat feature;

        @Builder
        public Update(Long id, String name, Integer price, Long storeId,
                      MenuType type, MenuFeat feature,
                      String storedFileName, String uploadImgUrl, MultipartFile file,
                      int discountPrice, LocalDateTime discountStart, LocalDateTime discountEnd) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.storeId = storeId;
            this.type = type;
            this.feature = feature;
            this.storedFileName = storedFileName;
            this.uploadImgUrl = uploadImgUrl;
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
            this.type = menu.getType();
            this.feature = menu.getFeature();
            this.storedFileName = menu.getFile().getStoredFileName();
            this.uploadImgUrl = menu.getFile().getUploadImageUrl();
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
                    .type(type)
                    .feature(feature)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    public static class Simple {
        @NotBlank
        private String name;

        @NotNull
        private Integer price;

        @NotNull
        private int discountPrice;

        @NotBlank
        private MenuType type;

        private MenuFeat feature;

        private String storedFileName;

        private String uploadImgUrl;

        @Builder
        public Simple(String name, Integer price, int discountPrice,
                      MenuType type, MenuFeat feature, String storedFileName, String uploadImgUrl) {
            this.name = name;
            this.price = price;
            this.discountPrice = discountPrice;
            this.type = type;
            this.feature = feature;
            this.storedFileName = storedFileName;
            this.uploadImgUrl = uploadImgUrl;
        }

        public static MenuDTO.Simple of(Menu entity) {
            return Simple.builder()
                    .name(entity.getName())
                    .price(entity.getPrice())
                    .discountPrice(entity.getDiscountPrice())
                    .type(entity.getType())
                    .feature(entity.getFeature())
                    .storedFileName(entity.getFile().getStoredFileName())
                    .uploadImgUrl(entity.getFile().getUploadImageUrl())
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

        @NotBlank
        private MenuType type;

        private String storedFileName;

        private LocalDateTime discountStart;

        private LocalDateTime discountEnd;

        public Detail(Menu menu) {
            this.name = menu.getName();
            this.price = menu.getPrice();
            this.discountPrice = menu.getDiscountPrice();
            this.type = menu.getType();
            this.storedFileName = menu.getFile().getStoredFileName();
            this.discountStart = menu.getDiscountStart();
            this.discountEnd = menu.getDiscountEnd();
        }
    }

}