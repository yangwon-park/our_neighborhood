package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.file.FileStore;
import ywphsm.ourneighbor.service.MenuService;

import java.io.IOException;
import java.net.MalformedURLException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/menu")
public class MenuApiController {

    private final MenuService menuService;

    private final FileStore fileStore;

    @PostMapping
    public Long save(MenuDTO.Add dto) throws IOException {

        log.info("dto={}", dto);
        return menuService.save(dto);
    }

    @PutMapping("/{storeId}")
    public Long update(@PathVariable Long storeId, MenuDTO.Update dto) throws IOException {

        return menuService.update(storeId, dto);
    }

    @DeleteMapping("/{storeId}")
    public Long delete(@PathVariable Long storeId, @RequestParam Long menuId) {
        log.info("menuId={}", menuId);
        return menuService.delete(menuId);
    }


    // 메뉴 이미지 출력
    @GetMapping("/{fileName}")
    public Resource downloadImage(@PathVariable String fileName) throws MalformedURLException {

        return new UrlResource("file:" + fileStore.getFullPath(fileName));
    }
}
