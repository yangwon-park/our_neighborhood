package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.dto.category.CategoryDTO;
import ywphsm.ourneighbor.service.CategoryService;
import ywphsm.ourneighbor.service.StoreService;

import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StoreApiController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    @GetMapping("/getImages")
    public List<List<String>> getImages(@CookieValue(value = "lat", required = false, defaultValue = "") String lat,
                                        @CookieValue(value = "lon", required = false, defaultValue = "") String lon) {

        List<CategoryDTO.Simple> rootCategoryList = categoryService.findByDepth(1L);

        List<List<String>> categoryImageList = new ArrayList<>();

        if (lat != null && lon != null) {
            for (CategoryDTO.Simple simple : rootCategoryList) {
                categoryImageList.add(storeService.getTop5ImageByCategories(
                        (simple.getCategoryId().toString()), Double.parseDouble(lat), Double.parseDouble(lon)));
            }
        }

        return categoryImageList;
    }

    @PostMapping("/seller/store")
    public Long save(@Validated StoreDTO.Add dto,
                     @RequestParam(value = "categoryId") List<Long> categoryId) throws IOException {

        List<Long> collect = categoryId.stream()
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());

        for (Long lon :collect){
            log.info("lon={}", lon);
        }

        List<Category> categoryList = collect.stream()
                                              .map(categoryService::findById)
                                              .collect(Collectors.toList());

        return storeService.save(dto, categoryList);
    }

    @PutMapping("/seller/store/{storeId}")
    public Long update(@PathVariable Long storeId, @Validated StoreDTO.Update dto,
                       @RequestParam List<Long> categoryId,
                       @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);
            if (!storeOwner) {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer);
            }
        }

        log.info("dto={}", dto);

        return storeService.update(storeId, dto, categoryId);
    }

    @PostMapping("/seller/store/editImage/{storeId}")
    public Long saveImage(@PathVariable Long storeId, @RequestParam MultipartFile file,
                            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);
            if (!storeOwner) {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer);
            }
        }

        return storeService.saveMainImage(storeId, file);
    }

    @PutMapping("/seller/store/editImage/{storeId}")
    public Long updateImage(@PathVariable Long storeId, @RequestParam MultipartFile file,
                            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);
            if (!storeOwner) {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer);
            }
        }

        return storeService.updateMainImage(storeId, file);
    }

    @DeleteMapping("/admin/store/{storeId}")
    public Long delete(@PathVariable Long storeId) {
        return storeService.delete(storeId);
    }
}
