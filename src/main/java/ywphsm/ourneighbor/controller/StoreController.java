package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.dto.StoreAddDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.dto.StoreDetailDTO;
import ywphsm.ourneighbor.service.StoreService;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/store")
@Slf4j
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

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

        StoreDetailDTO storeDetailDTO = new StoreDetailDTO(store);

        model.addAttribute("store", storeDetailDTO);
        return "store/detail";
    }

    @GetMapping("/addStore")
    public String addStore(Model model) {
        model.addAttribute("store", new StoreAddDTO());

        return "store/add_form";
    }

    @PostMapping("/addStore")
    public String addStore(@ModelAttribute StoreAddDTO storeAddDTO) {
        Store store = storeAddDTO.toEntity();

        storeService.saveStore(store);

        return "redirect:/map";
    }
}