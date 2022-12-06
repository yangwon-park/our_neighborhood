package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.config.ScriptUtils;
import ywphsm.ourneighbor.controller.form.CategorySimpleDTO;
import ywphsm.ourneighbor.domain.dto.*;
import ywphsm.ourneighbor.domain.dto.Member.MemberDTO;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagOfStoreDTO;
import ywphsm.ourneighbor.domain.member.MemberOfStore;
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

@Slf4j
@RequiredArgsConstructor
@Controller
public class StoreController {

    private final StoreService storeService;

    private final CategoryService categoryService;

    private final MenuService menuService;

    private final ReviewService reviewService;

    private final MemberService memberService;

    private final HashtagOfStoreService hashtagOfStoreService;

    private final RequestAddStoreService requestAddStoreService;

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
        StoreDTO.Detail storeDTO = new StoreDTO.Detail(store);

        List<CategorySimpleDTO> categorySimpleDTOList = storeDTO.getCategoryList().stream()
                .map(categoryOfStoreDTO ->
                        categoryService.findById(categoryOfStoreDTO.getCategoryId()))
                .map(CategorySimpleDTO::of).collect(Collectors.toList());

        List<HashtagOfStoreDTO.WithCount> hashtagGroupDTO = hashtagOfStoreService.findHashtagAndCountByStoreIdTop9(storeId);

        List<Menu> menuList = menuService.findByStoreIdWithoutMenuTypeIsMenuCaseByOrderByType(storeId);

        /*
            메뉴판 조회
         */
        List<String> menuImgList = menuService.findImageByMenuTypeIsMenu(storeId);

        List<MenuDTO.Detail> menuDTOList = menuList.stream()
                .map(MenuDTO.Detail::of).collect(Collectors.toList());

        // review paging
        Slice<ReviewMemberDTO> reviewMemberDTOS = reviewService.pagingReview(storeId, 0);
        List<ReviewMemberDTO> content = reviewMemberDTOS.getContent();

        double ratingAverage = reviewService.ratingAverage(storeId);

        // 찜, 스토어 수정 권한
        if (member != null) {
            // 찜
            boolean likeStatus = memberService.likeStatus(member.getId(), storeId);
            // 스토어 수정 권한
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

        model.addAttribute("store", storeDTO);
        model.addAttribute("menuList", menuDTOList);
        model.addAttribute("menuImgList", menuImgList);
        model.addAttribute("categoryList", categorySimpleDTOList);
        model.addAttribute("hashtagList", hashtagGroupDTO);

        // review
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
                ScriptUtils.alertAndBackPage(response, "해당 가게의 권한이 없습니다.");
            }
        }

        Store findStore = storeService.findById(storeId);
        StoreDTO.Update store = new StoreDTO.Update(findStore);

        List<CategorySimpleDTO> categorySimpleDTOList = store.getCategoryList().stream()
                .map(categoryOfStoreDTO ->
                        categoryService.findById(categoryOfStoreDTO.getCategoryId()))
                .map(CategorySimpleDTO::of).collect(Collectors.toList());

        model.addAttribute("store", store);
        model.addAttribute("categoryList", categorySimpleDTOList);

        return "store/edit_form";
    }

    @GetMapping("/admin/store-owner/edit/{storeId}")
    public String storeOwnerEdit(@PathVariable Long storeId, Model model) {
        List<MemberDTO.Detail> owners = storeService.findById(storeId).getMemberOfStoreList().stream()
                .filter(MemberOfStore::isMyStore)
                .map(memberOfStore -> new MemberDTO.Detail(memberOfStore.getMember()))
                .collect(Collectors.toList());

        int count = 0;
        if (!owners.isEmpty()) {
            count = owners.size();
        }

        model.addAttribute("owners", owners);
        model.addAttribute("count", count);

        return "store/store_owner_edit";
    }

    @GetMapping("/user/request-add-store")
    public String requestAddStore(Model model) {
        model.addAttribute("request", new RequestAddStoreDTO.Add());
        return "store/request_add_store";
    }

    @GetMapping("/admin/request-add-store-list")
    public String requestAddStoreList(Model model, Integer page) {
        if (page == null) {
            page = 0;
        } else {
            page--;
        }
        Page<RequestAddStoreDTO.Detail> requestAddStoreDTOS = requestAddStoreService.pagingRequestAddStore(page);
        List<RequestAddStoreDTO.Detail> content = requestAddStoreDTOS.getContent();

        PageMakeDTO pageMakeDTO = new PageMakeDTO(page, 10, requestAddStoreDTOS.getTotalElements());

        model.addAttribute("count", requestAddStoreDTOS.getTotalElements());
        model.addAttribute("request", content);
        model.addAttribute("pageMake", pageMakeDTO);

        return "store/request_add_store_list";
    }

    @GetMapping("/recommend/store/list")
    public String recommendStoreList() {
        return "store/list";
    }
}