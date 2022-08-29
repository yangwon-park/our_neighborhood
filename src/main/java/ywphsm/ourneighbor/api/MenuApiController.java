package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.MenuAddDTO;
import ywphsm.ourneighbor.domain.file.FileStore;
import ywphsm.ourneighbor.service.MenuService;

import java.io.IOException;
import java.net.MalformedURLException;

@RequiredArgsConstructor
@Slf4j
@RestController
public class MenuApiController {

    private final MenuService menuService;
    private final FileStore fileStore;

    @PostMapping(value = "/menu/add")
    public Long save(MenuAddDTO menuAddDTO) throws IOException {

        log.info("menuAddDTO={}", menuAddDTO);
        return menuService.saveMenu(menuAddDTO);
    }

    @PostMapping(value = "/menu/add2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long save2(MenuAddDTO menuAddDTO) throws IOException {
        log.info("menuAddDTO={}", menuAddDTO);
        return menuService.saveMenu(menuAddDTO);
    }

    // 메뉴 이미지 출력
    @GetMapping("/menu/{fileName}")
    public Resource downloadImage(@PathVariable String fileName) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(fileName));
    }
}
