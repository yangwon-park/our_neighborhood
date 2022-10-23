package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ywphsm.ourneighbor.domain.dto.category.CategoryDTO;
import ywphsm.ourneighbor.service.CategoryService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/admin/category/add")
    public String addCategory(Model model) {
        List<CategoryDTO> all = categoryService.findAll();
        log.info("all={}", all);
        model.addAttribute("all", all);
        model.addAttribute("category", new CategoryDTO());

        return "category/add_form";
    }
}
