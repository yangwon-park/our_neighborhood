package ywphsm.ourneighbor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ywphsm.ourneighbor.domain.dto.category.CategoryDTO;
import ywphsm.ourneighbor.domain.search.StoreSearchCond;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.StoreService;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final StoreService storeService;

    private final CategoryService categoryService;

    @GetMapping("/")
    public String index(Model model,
                        @CookieValue(value = "lat", required = false) String lat,
                        @CookieValue(value = "lon", required = false) String lon) {

        List<CategoryDTO.Simple> rootCategoryList = categoryService.findByDepth(1L);


        if (lat != null && lon != null) {
            // 최선의 코드인 것 같지가 않음
            // 더 좋은 방법이 생각나거나 알게 된다면 추후에 리팩토링하자
            List<String> restaurantImages = storeService.getTop5ImageByCategories((rootCategoryList.get(0).getCategoryId().toString()),
                    Double.parseDouble(lat), Double.parseDouble(lon));

            List<String> cafeImages = storeService.getTop5ImageByCategories((rootCategoryList.get(1).getCategoryId().toString()),
                    Double.parseDouble(lat), Double.parseDouble(lon));

            List<String> barImages = storeService.getTop5ImageByCategories((rootCategoryList.get(2).getCategoryId().toString()),
                    Double.parseDouble(lat), Double.parseDouble(lon));

            List<String> leisureImages = storeService.getTop5ImageByCategories((rootCategoryList.get(3).getCategoryId().toString()),
                    Double.parseDouble(lat), Double.parseDouble(lon));


            model.addAttribute("restaurantImages", restaurantImages);
            model.addAttribute("cafeImages", cafeImages);
            model.addAttribute("barImages", barImages);
            model.addAttribute("leisureImages", leisureImages);
        }

        model.addAttribute("rootCategoryList", rootCategoryList);

        return "index";
    }

    // 검색 뷰페이지 임시
    @GetMapping("/map")
    public String map(@ModelAttribute("storeSearchCond") StoreSearchCond storeSearchCond) {
        return "map";
    }
}