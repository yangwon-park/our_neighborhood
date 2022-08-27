package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ywphsm.ourneighbor.domain.dto.MenuAddDTO;
import ywphsm.ourneighbor.service.MenuService;
import ywphsm.ourneighbor.service.StoreService;

@Controller
@RequestMapping("/menu")
@Slf4j
@RequiredArgsConstructor
public class MenuController {

    private final StoreService storeService;
    private final MenuService menuService;

    @GetMapping("/addMenu/{storeId}")
    public String addMenu(@PathVariable Long storeId, Model model) {
        MenuAddDTO dto = new MenuAddDTO();
        dto.setStoreId(storeId);
        model.addAttribute("menu", dto);
        return "menu/add_form";
    }







}
