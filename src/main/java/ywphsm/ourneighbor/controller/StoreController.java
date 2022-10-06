package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.controller.form.CategorySimpleDTO;
import ywphsm.ourneighbor.domain.dto.CategoryOfStoreDTO;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Controller
public class StoreController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    @ModelAttribute("menuTypes")
    public MenuType[] menuTypes() {
        return MenuType.values();
    }

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

    @GetMapping("/store/{storeId}")
    public String storeDetail(@PathVariable Long storeId, Model model) {
        Store store = storeService.findById(storeId);

        StoreDTO.Detail dto = new StoreDTO.Detail(store);

        List<MenuDTO.Detail> menuList = dto.getMenuList();

        for (MenuDTO.Detail detail : menuList) {
            log.info("detail={}", detail.getStoredFileName());
        }

        List<CategoryOfStoreDTO> categoryList = dto.getCategoryList();

//        List<CategorySimpleDTO> dtoList = new ArrayList<>();
//        for (CategoryOfStoreDTO categoryOfStoreDTO : categoryList) {
//            Category category = categoryService.findById(categoryOfStoreDTO.getCategoryId());
//            CategorySimpleDTO dto = CategorySimpleDTO.of(category);
//            dtoList.add(dto);
//        }

        List<CategorySimpleDTO> dtoList = categoryList.stream()
                .map(categoryOfStoreDTO ->
                        categoryService.findById(categoryOfStoreDTO.getCategoryId()))
                .map(CategorySimpleDTO::of).collect(Collectors.toList());

        model.addAttribute("store", dto);
        model.addAttribute("categoryList", dtoList);
        return "store/detail";
    }

    @GetMapping("/seller/store/add")
    public String addStore(Model model) {
        model.addAttribute("store", new StoreDTO.Add());
        return "store/add_form";
    }

    @GetMapping("/seller/store/edit/{storeId}")
    public String editStore(@PathVariable Long storeId, Model model) {
        Store findStore = storeService.findById(storeId);
        StoreDTO.Update store = new StoreDTO.Update(findStore);

        model.addAttribute("store", store);

        return "store/edit_form";
    }

    @GetMapping("/admin/store/list")
    public String getStoreList(Model model) {
        model.addAttribute("store", new StoreDTO.Detail());
        return "store/list_by_admin";
    }
}