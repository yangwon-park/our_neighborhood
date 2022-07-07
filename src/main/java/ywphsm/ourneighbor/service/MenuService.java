package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.repository.MenuRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    // 메뉴 등록
    @Transactional
    public Long saveMenu(Menu menu) {
        menuRepository.save(menu);
        return menu.getId();
    }

    // 메뉴 하나 조회
    public Menu findOne(Long menuId) {
        return menuRepository.findById(menuId).orElseGet(null);
    }

    // 전체 메뉴 조회
    public List<Menu> findMenus() {
        return menuRepository.findAll();
    }

    // 메뉴 이름으로 조회
    public Menu findByName(String name) {
        return menuRepository.findByName(name);
    }
}
