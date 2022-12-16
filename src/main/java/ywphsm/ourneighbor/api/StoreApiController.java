package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.ParseException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.config.ScriptUtils;
import ywphsm.ourneighbor.domain.dto.RequestAddStoreDTO;
import ywphsm.ourneighbor.domain.dto.store.StoreDTO;
import ywphsm.ourneighbor.service.RequestAddStoreService;
import ywphsm.ourneighbor.service.store.DaysService;
import ywphsm.ourneighbor.service.store.StoreService;

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

    private final RequestAddStoreService requestAddStoreService;

    private final DaysService daysService;

    @PostMapping("/seller/store")
    public Long save(@Validated StoreDTO.Add dto,
                     @RequestParam(value = "categoryId") List<Long> categoryIdList,
                     @RequestParam(value = "daysId", required = false) List<Long> daysIdList) throws IOException, ParseException {

        return storeService.save(dto, categoryIdList, daysIdList);
    }

    @PutMapping("/seller/store/{storeId}")
    public Long update(@PathVariable Long storeId, @Validated StoreDTO.Update dto,
                       @RequestParam(value = "categoryId") List<Long> categoryIdList,
                       @RequestParam(value = "daysId", required = false) List<Long> daysIdList,
                       @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);
            if (!storeOwner) {
                ScriptUtils.alertAndBackPage(response, "해당 가게의 권한이 없습니다.");
            }
        }

        return storeService.update(storeId, dto, categoryIdList, daysIdList);
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

    @PostMapping("/admin/store-owner/add")
    public String addStoreOwner(String userId, Long storeId) {
        return storeService.addStoreOwner(userId, storeId);
    }

    @DeleteMapping("/admin/store-owner/delete")
    public String deleteStoreOwner(Long memberId, Long storeId) {
        return storeService.deleteStoreOwner(memberId, storeId);
    }

    @PostMapping("/user/request-add-store")
    public Long requestAddStore(RequestAddStoreDTO.Add requestAddStoreDTO, Long memberId) {
        return requestAddStoreService.save(requestAddStoreDTO, memberId);
    }

    @DeleteMapping("/admin/request-add-store/delete")
    public Long deleteRequestAddStore(Long requestAddStoreId) {
        return requestAddStoreService.delete(requestAddStoreId);
    }

    @GetMapping("/user/days")
    public List<Long> getDaysByStoreId(Long storeId) {
        return daysService.getDaysByStoreId(storeId);
    }
}
