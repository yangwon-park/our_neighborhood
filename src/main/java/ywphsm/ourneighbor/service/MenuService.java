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

    @Transactional
    public Long saveMenu(Menu menu) {
        menuRepository.save(menu);
        return menu.getId();
    }

    public Menu findOne(Long menuId) {
        return menuRepository.findById(menuId).orElseGet(null);
    }

    public List<Menu> findMenus() {
        return menuRepository.findAll();
    }

    public Menu findByName(String name) {
        return menuRepository.findByName(name);
    }
}
