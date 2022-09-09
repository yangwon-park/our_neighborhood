package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/categories2")
    public ResultClass<?> findAllCategories2() {
        List<CategoryDTO> categories = categoryService.findAllCategoriesHier();
        return new ResultClass<>(categories);
    }

    @PostMapping("/category/add")
    public Long save(CategoryDTO dto) {
        log.info("========================================================");
        log.info("dto={},", dto);
        log.info("========================================================");
        return categoryService.saveCategory(dto);
    }
}
