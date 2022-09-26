package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.Category;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/store")
public class StoreApiController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    @PostMapping
    public Long save(@Validated StoreDTO.Add dto, @RequestParam(value = "categoryId") List<Long> categoryId) {

        log.info("dto={}", dto);
        for (Long id : categoryId) {
            log.info("id={}", id);
        }

        List<Category> categoryList = categoryId.stream()
                .map(categoryService::findById)
                .collect(Collectors.toList());

        return storeService.save(dto, categoryList);
    }

    @PutMapping("/{storeId}")
    public Long update(@PathVariable Long storeId, @Validated StoreDTO.Update dto,
                       @RequestParam List<Long> categoryId) {

        log.info("dto={}", dto);

        return storeService.update(storeId, dto, categoryId);
    }

    @DeleteMapping("/{storeId}")
    public Long delete(@PathVariable Long storeId) {
        return storeService.delete(storeId);
    }
}
