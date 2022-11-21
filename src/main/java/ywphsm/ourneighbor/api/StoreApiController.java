package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.ParseException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.config.ScriptUtils;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.service.StoreService;

import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StoreApiController {

    private final StoreService storeService;

    @PostMapping("/seller/store")
    public Long save(@Validated StoreDTO.Add dto,
                     @RequestParam(value = "categoryId") List<Long> categoryIdList) throws IOException, ParseException {
        return storeService.save(dto, categoryIdList);
    }

    @PutMapping("/seller/store/{storeId}")
    public Long update(@PathVariable Long storeId, @Validated StoreDTO.Update dto,
                       @RequestParam(value = "categoryId") List<Long> categoryIdList,
                       @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);
            if (!storeOwner) {
                ScriptUtils.alertAndBackPage(response, "해당 가게의 권한이 없습니다.");
            }
        }

        return storeService.update(storeId, dto, categoryIdList);
    }

    @PostMapping("/seller/store/edit-image/{storeId}")
    public Long saveMainImage(@PathVariable Long storeId, @RequestParam MultipartFile file,
                            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);
            if (!storeOwner) {
                ScriptUtils.alertAndBackPage(response, "해당 가게의 권한이 없습니다.");
            }
        }

        return storeService.saveMainImage(storeId, file);
    }

    @PutMapping("/seller/store/edit-image/{storeId}")
    public Long updateMainImage(@PathVariable Long storeId, @RequestParam MultipartFile file,
                            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);
            if (!storeOwner) {
                ScriptUtils.alertAndBackPage(response, "해당 가게의 권한이 없습니다.");
            }
        }

        return storeService.updateMainImage(storeId, file);
    }

    @DeleteMapping("/admin/store/{storeId}")
    public Long delete(@PathVariable Long storeId) {
        return storeService.delete(storeId);
    }

    @PostMapping("/admin/storeOwner/add")
    public String addStoreOwner(String userId, Long storeId) {
        return storeService.addStoreOwner(userId, storeId);
    }

    @DeleteMapping("/admin/storeOwner/delete")
    public String deleteStoreOwner(Long memberId, Long storeId) {
        return storeService.deleteStoreOwner(memberId, storeId);
    }
}
