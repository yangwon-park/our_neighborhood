package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.file.FileStore;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.menu.MenuRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.io.IOException;

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
    public Long save(MenuDTO.Add menuAddDTO) throws IOException {
        Store linkedStore = storeRepository.findById(menuAddDTO.getStoreId()).orElseThrow(() -> new IllegalArgumentException("해당 매장이 없어요"));

        UploadFile newUploadFile = fileStore.storeFile(menuAddDTO.getFile());

        log.info("fileName={}", newUploadFile.getStoredFileName());
        log.info("fileName={}", newUploadFile.getUploadedFileName());

        Menu menu = menuAddDTO.toEntity(linkedStore);

        newUploadFile.addMenu(menu);

        linkedStore.addMenu(menu);

        return menuRepository.save(menu).getId();
    }

    // 메뉴 수정
    @Transactional
    public Long update(Long storeId, MenuDTO.Update dto) throws IOException {

        // 새로 업로드한 파일 UploadFile로 생성
        UploadFile newUploadFile = fileStore.storeFile(dto.getFile());

        // 수정하고자 하는 메뉴 찾음
        Menu menu = menuRepository.findById(dto.getId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 메뉴입니다. id = " + dto.getId()));

        // dto => Entity 변환
        Menu entity = dto.toEntity();

        // 기존 메뉴 업데이트
        menu.updateWithoutImage(entity);

        // 기존 메뉴의 업로드 파일 찾음
        UploadFile file = menu.getFile();

        // 메뉴의 저장명, 업로드명 업데이트
        file.updateUploadedFileName(newUploadFile.getStoredFileName(), newUploadFile.getUploadedFileName());

        return storeId;
    }

    @Transactional
    public Long delete(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 메뉴입니다. id = " + menuId));

        log.info("menu={}", menu);

        menuRepository.delete(menu);

        return menuId;
    }
    // 메뉴 하나 조회

    public Menu findById(Long menuId) {
        return menuRepository.findById(menuId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 메뉴입니다. id = " + menuId));
    }

    public boolean checkMenuDuplicate(String name, Store store) {
        return menuRepository.existsByNameAndStore(name, store);
    }
}