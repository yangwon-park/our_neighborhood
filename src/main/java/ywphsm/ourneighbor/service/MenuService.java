package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.file.FileStore;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.menu.MenuRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final FileStore fileStore;

    // 메뉴 등록
    @Transactional
    public Long saveMenu(MenuDTO.Add menuAddDTO) throws IOException {
        Store findStore = storeRepository.findById(menuAddDTO.getStoreId()).orElseThrow(() -> new IllegalArgumentException("해당 매장이 없어요"));

        UploadFile storedImage = fileStore.storeFile(menuAddDTO.getImage());

        Menu menu = menuAddDTO.toEntity(findStore);
        storedImage.addMenu(menu);
        findStore.addMenu(menu);

        return menuRepository.save(menu).getId();
    }

    // 메뉴 하나 조회
    public Menu findOne(Long menuId) {
        return menuRepository.findById(menuId).orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
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
