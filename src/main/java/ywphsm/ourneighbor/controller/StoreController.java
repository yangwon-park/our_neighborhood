package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ywphsm.ourneighbor.controller.form.CategorySimpleDTO;
import ywphsm.ourneighbor.domain.Category;
import ywphsm.ourneighbor.domain.dto.CategoryDTO;
import ywphsm.ourneighbor.domain.dto.CategoryOfStoreDTO;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    @ModelAttribute("offDays")
    public Map<String, String> offDays() {
        Map<String, String> offDays = new LinkedHashMap<>();

        offDays.put("일", "일요일");
        offDays.put("월", "월요일");
        offDays.put("화", "화요일");
        offDays.put("수", "수요일");
        offDays.put("목", "목요일");
        offDays.put("금", "금요일");
        offDays.put("토", "토요일");

        return offDays;
    }

    @GetMapping("/{storeId}")
    public String storeDetail(@PathVariable Long storeId, Model model) {
        Store store = storeService.findOne(storeId);
        StoreDTO.Detail dto = new StoreDTO.Detail(store);
        List<CategoryOfStoreDTO> categoryList = dto.getCategoryList();

//        List<CategorySimpleDTO> dtoList = new ArrayList<>();
//        for (CategoryOfStoreDTO categoryOfStoreDTO : categoryList) {
//            Category id = categoryService.findById(categoryOfStoreDTO.getCategoryId());
//            CategorySimpleDTO of = CategorySimpleDTO.of(id);
//            dtoList.add(of);
//        }

        List<CategorySimpleDTO> dtoList = categoryList.stream()
                .map(categoryOfStoreDTO ->
                        categoryService.findById(categoryOfStoreDTO.getCategoryId()))
                .map(CategorySimpleDTO::of).collect(Collectors.toList());

        model.addAttribute("store", dto);
        model.addAttribute("categoryList", dtoList);
        return "store/detail";
    }

    @GetMapping("/add")
    public String addStore(Model model) {
        model.addAttribute("store", new StoreDTO.Add());
        return "store/add_form";
    }

    @PostMapping("/add")
    public String addStore(@ModelAttribute StoreDTO.Add storeAddDTO,
                           @RequestParam(value="categoryId") List<Long> categoryId) {
        List<Category> categoryList = new ArrayList<>();

        for (Long id : categoryId) {
            Category category = categoryService.findById(id);
            log.info("category={}", category.getCategoryOfStoreList());
            categoryList.add(category);
        }

        storeService.saveStore(storeAddDTO, categoryList);

        return "redirect:/";
    }

}