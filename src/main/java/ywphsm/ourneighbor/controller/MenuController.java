package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.menu.MenuFeat;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.StoreService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MenuController {

    private final StoreService storeService;

    @ModelAttribute("menuTypes")
    public MenuType[] menuTypes() {
        return MenuType.values();
    }

    @ModelAttribute("menuFeats")
    public MenuFeat[] menuFeats() {
        return MenuFeat.values();
    }

    @GetMapping("/seller/menu/add/{storeId}")
    public String addMenu(@PathVariable Long storeId, Model model,
                          @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                          HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);

            if (!storeOwner) {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer);
            }
        }

        MenuDTO.Add dto = new MenuDTO.Add();
        dto.setStoreId(storeId);
        model.addAttribute("menu", dto);
        return "menu/add_form";
    }

    @GetMapping("/seller/menu/edit/{storeId}")
    public String editMenu(@PathVariable Long storeId, Model model,
                           @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                           HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);

            if (!storeOwner) {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer);
            }
        }

        Store findStore = storeService.findById(storeId);

        List<Menu> list = findStore.getMenuList();
        List<MenuDTO.Update> menuList = list.stream()
                .map(MenuDTO.Update::new)
                .collect(Collectors.toList());

        model.addAttribute("menuList", menuList);

        return "menu/edit_form";
    }
}