package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Controller
public class MenuController {

    private final StoreService storeService;

    @ModelAttribute("menuTypes")
    public MenuType[] menuTypes() {
        return MenuType.values();
    }

    @GetMapping("/seller/menu/add/{storeId}")
    public String addMenu(@PathVariable Long storeId, Model model) {
        MenuDTO.Add dto = new MenuDTO.Add();
        dto.setStoreId(storeId);
        model.addAttribute("menu", dto);
        return "menu/add_form";
    }

    @GetMapping("/seller/menu/edit/{storeId}")
    public String editMenu(@PathVariable Long storeId, Model model) {
        Store findStore = storeService.findById(storeId);

        List<Menu> list = findStore.getMenuList();
        List<MenuDTO.Update> menuList = list.stream()
                .map(MenuDTO.Update::new)
                .collect(Collectors.toList());

        log.info("menuList={}", menuList);

        model.addAttribute("menuList", menuList);

        return "/menu/edit_form";
    }
}