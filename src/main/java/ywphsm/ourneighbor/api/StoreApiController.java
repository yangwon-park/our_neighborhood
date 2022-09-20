package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.Category;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/store")
public class StoreApiController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    @PutMapping("/edit/{storeId}")
    public Long update(@PathVariable Long storeId, StoreDTO.Update dto,
                       @RequestParam List<Long> categoryId) {

        List<Category> categoryList = categoryId.stream()
                .map(categoryService::findById).collect(Collectors.toList());

        return storeService.update(storeId, dto, categoryList);
    }
}
