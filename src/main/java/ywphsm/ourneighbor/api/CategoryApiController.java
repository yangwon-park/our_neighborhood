package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.Category;
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


    @PostMapping("/category/add")
    public Long save(@Validated CategoryDTO dto) {
        return categoryService.saveCategory(dto);
    }

    @DeleteMapping("/category/delete/{categoryId}")
    public Long delete(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);

        return categoryId;
    }
}
