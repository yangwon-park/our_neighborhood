package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.StoreService;

@Controller
@RequestMapping("/menu")
@Slf4j
@RequiredArgsConstructor
public class MenuController {

    private StoreService storeService;

    @GetMapping("/addMenu/{storeId}")
    public String addMenu(@PathVariable Long storeId, Model model) {

        return "menu/add_form";
    }
}
