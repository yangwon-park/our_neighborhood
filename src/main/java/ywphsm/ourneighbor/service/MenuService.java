package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.dto.MenuAddDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.menu.MenuRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    // 메뉴 등록
    @Transactional
    public Long saveMenu(MenuAddDTO menuAddDTO) {
        Store findStore = storeRepository.findById(menuAddDTO.getStoreId()).orElseThrow(() -> new IllegalArgumentException("해당 매장이 없어요"));
        return menuRepository.save(menuAddDTO.toEntity(findStore)).getId();
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
