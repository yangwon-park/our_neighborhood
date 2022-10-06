package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.file.FileStore;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.service.MenuService;
import ywphsm.ourneighbor.service.StoreService;
import ywphsm.ourneighbor.service.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MenuApiController {

    private final MenuService menuService;
    private final StoreService storeService;

    private final FileStore fileStore;

    @GetMapping("/menu/check")
    public ResponseEntity<Boolean> checkMenuDuplicate(String name, Long storeId) {
        Store store = storeService.findById(storeId);

        return ResponseEntity.ok(menuService.checkMenuDuplicate(name, store));
    }

    @PostMapping("/menu")
    public Long save(MenuDTO.Add dto,
                     @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                     HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, dto.getStoreId());

            if (!storeOwner) {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer);
            }
        }

        log.info("dto={}", dto);
        return menuService.save(dto);
    }

    @PutMapping("/menu/{storeId}")
    public Long update(@PathVariable Long storeId, MenuDTO.Update dto,
                       @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);

            if (!storeOwner) {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer);
            }
        }

        return menuService.update(storeId, dto);
    }

    @DeleteMapping("/menu/{storeId}")
    public Long delete(@PathVariable Long storeId, @RequestParam Long menuId,
                       @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (member.getRole().equals(Role.SELLER)) {
            boolean storeOwner = storeService.OwnerCheck(member, storeId);

            if (!storeOwner) {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer);
            }
        }

        log.info("menuId={}", menuId);
        return menuService.delete(menuId);
    }


    // 메뉴 이미지 출력
    @GetMapping("/menu/{fileName}")
    public Resource downloadImage(@PathVariable String fileName) throws MalformedURLException {

        return new UrlResource("file:" + fileStore.getFullPath(fileName));
    }
}
