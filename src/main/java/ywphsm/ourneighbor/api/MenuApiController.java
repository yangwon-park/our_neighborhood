package ywphsm.ourneighbor.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ywphsm.ourneighbor.domain.dto.MenuAddDTO;
import ywphsm.ourneighbor.service.MenuService;

@RequiredArgsConstructor
@RestController
public class MenuApiController {

    private final MenuService menuService;

    @PostMapping(value = "/menu/add")
    public Long save(@RequestBody MenuAddDTO menuAddDTO) {
        return menuService.saveMenu(menuAddDTO);
    }
}
