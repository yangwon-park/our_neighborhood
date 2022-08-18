package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.MenuAddDTO;
import ywphsm.ourneighbor.service.MenuService;

@RequiredArgsConstructor
@Slf4j
@RestController
public class MenuApiController {

    private final MenuService menuService;

    @PostMapping(value = "/menu/add")
    public Long save(@RequestBody MenuAddDTO menuAddDTO) {

        log.info("menuAddDTO={}", menuAddDTO);
        return menuService.saveMenu(menuAddDTO);
    }
}
