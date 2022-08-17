package ywphsm.ourneighbor.domain.dto;

import lombok.Data;

@Data
public class MenuAddDTO {

    private Long menuId;
    private String name;
    private Integer price;

    private Long storeId;
}
