package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.controller.form.CategorySimpleDTO;
import ywphsm.ourneighbor.domain.dto.*;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.domain.menu.MenuFeat;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.domain.store.ParkAvailable;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.*;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final MenuService menuService;
    private final ReviewService reviewService;

    private final MemberService memberService;

    @ModelAttribute("menuTypes")
    public MenuType[] menuTypes() {
        return MenuType.values();
    }

    @ModelAttribute("menuFeats")
    public MenuFeat[] menuFeats() {
        return MenuFeat.values();
    }

    @ModelAttribute("parkAva")
    public ParkAvailable[] parkAvailables() {
        return ParkAvailable.values();
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
    public String storeDetail(@PathVariable Long storeId, Model model,
                              @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        Store store = storeService.findById(storeId);
        StoreDTO.Detail dto = new StoreDTO.Detail(store);

        List<Menu> menuList = menuService.findByStoreIdCaseByOrderByType(storeId);
        List<MenuDTO.Simple> menuDTOList = menuList.stream()
                .map(MenuDTO.Simple::of).collect(Collectors.toList());

        List<CategorySimpleDTO> dtoList = dto.getCategoryList().stream()
                .map(categoryOfStoreDTO ->
                        categoryService.findById(categoryOfStoreDTO.getCategoryId()))
                .map(CategorySimpleDTO::of).collect(Collectors.toList());

        //review paging
        Slice<ReviewMemberDTO> reviewMemberDTOS = reviewService.pagingReview(storeId, 0);
        List<ReviewMemberDTO> content = reviewMemberDTOS.getContent();
        log.info("content={}", content);
        double ratingAverage = reviewService.ratingAverage(storeId);

        //찜, 스토어 수정 권한
        if (member != null) {
            //찜
            boolean likeStatus = memberService.likeStatus(member.getId(), storeId);
            //스토어 수정 권한
            boolean storeRole = false;
            if (member.getRole().equals(Role.SELLER)) {
                boolean storeOwner = storeService.OwnerCheck(member, storeId);
                if (storeOwner) {
                    storeRole = true;
                }
            }
            model.addAttribute("likeStatus", likeStatus);
            model.addAttribute("storeRole", storeRole);
        }


        model.addAttribute("store", dto);
        model.addAttribute("menus", menuDTOList);
        model.addAttribute("categoryList", dtoList);
        //review
        model.addAttribute("review", content);
        model.addAttribute("ratingAverage", ratingAverage);

        return "store/detail";
    }

    @GetMapping("/seller/store/add")
    public String addStore(Model model,
                           @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {
        StoreDTO.Add add = new StoreDTO.Add();
        add.setMemberId(member.getId());
        model.addAttribute("store", add);
        return "store/add_form";
    }

    @GetMapping("/seller/store/edit/{storeId}")
    public String editStore(@PathVariable Long storeId, Model model,
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