package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.MenuAddDTO;
import ywphsm.ourneighbor.service.MenuService;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@RestController
public class MenuApiController {

    private final MenuService menuService;

    @PostMapping(value = "/menu/add")
    public Long save(@RequestBody MenuAddDTO menuAddDTO) throws IOException {

        log.info("menuAddDTO={}", menuAddDTO);
        return menuService.saveMenu(menuAddDTO);
    }

    @PostMapping(value = "/menu/add2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long save2(MenuAddDTO menuAddDTO) throws IOException {
        log.info("menuAddDTO={}", menuAddDTO);
        return menuService.saveMenu(menuAddDTO);
    }
}
