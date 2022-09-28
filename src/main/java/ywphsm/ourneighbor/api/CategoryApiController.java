package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.dto.CategoryDTO;
import ywphsm.ourneighbor.service.CategoryService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CategoryApiController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResultClass<?> findAllCategories() {
        List<CategoryDTO> categories = categoryService.findAll();
        return new ResultClass<>(categories);
    }

    @GetMapping("/categoriesHier")
    public CategoryDTO findAllCategoriesHier() {
        return categoryService.findAllCategoriesHier().get(0);
    }

    @GetMapping("/categoryCheck")
    public ResponseEntity<Boolean> checkCategoryDuplicate(String name, Long parentId) {
        Category parent = categoryService.findById(parentId);
        return ResponseEntity.ok(categoryService.checkCategoryDuplicate(name, parent));
    }

    // @RequestBody 생략 시, Test에서 받아오질 못함
    @PostMapping(value = "/category", produces = "application/json;")
    public Long save(@RequestBody CategoryDTO dto) {
        return categoryService.save(dto);
    }

    @DeleteMapping("/category/{categoryId}")
    public Long delete(@PathVariable Long categoryId) {
        return categoryService.delete(categoryId);
    }
}
