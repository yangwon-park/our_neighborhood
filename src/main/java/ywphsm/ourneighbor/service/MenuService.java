package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.file.AwsS3FileStore;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.file.FileStore;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.hashtag.HashtagRepository;
import ywphsm.ourneighbor.repository.hashtag.hashtagofmenu.HashtagOfMenuRepository;
import ywphsm.ourneighbor.repository.menu.MenuRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu.linkHashtagAndMenu;
import static ywphsm.ourneighbor.domain.hashtag.HashtagOfStore.linkHashtagAndStore;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    private final StoreRepository storeRepository;

    private final AwsS3FileStore awsS3FileStore;

    private final HashtagRepository hashtagRepository;

    private final HashtagOfMenuRepository hashtagOfMenuRepository;

    private final FileStore fileStore;

    // 메뉴 등록
    @Transactional
    public Long save(MenuDTO.Add dto) throws IOException, ParseException {

        Store linkedStore = storeRepository.findById(dto.getStoreId()).orElseThrow(
                () -> new IllegalArgumentException("해당 매장이 없습니다. id = " + dto.getStoreId()));

        UploadFile newUploadFile = awsS3FileStore.storeFile(dto.getFile());

        Menu menu = dto.toEntity(linkedStore);

        newUploadFile.addMenu(menu);

        linkedStore.addMenu(menu);

        Long savedMenuId = menuRepository.save(menu).getId();

        // 해쉬태그 저장 로직
        if (!dto.getHashtag().isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(dto.getHashtag());

            Menu savedMenu = menuRepository.findById(savedMenuId).orElseThrow(
                    () -> new IllegalArgumentException("해당 메뉴가 없습니다. id = " + savedMenuId));

            for (Object object : array) {
                JSONObject jsonObject = (JSONObject)object;

                HashtagDTO hashtagDTO = HashtagDTO.builder()
                        .name(jsonObject.get("value").toString())
                        .build();

                boolean duplicateCheck = hashtagRepository.existsByName(hashtagDTO.getName());

                Hashtag newHashtag;

                if (!duplicateCheck) {
                    newHashtag = hashtagRepository.save(hashtagDTO.toEntity());
                } else {
                    newHashtag = hashtagRepository.findByName(hashtagDTO.getName());
                }

                linkHashtagAndMenu(newHashtag, savedMenu);
            }
        }

        return savedMenuId;
    }

    // 메뉴 수정
    @Transactional
    public Long update(Long storeId, MenuDTO.Update dto) throws IOException, ParseException {
        // 전달받은 dto => Entity 변환
        Menu entity = dto.toEntity();

        // 수정하고자 하는 메뉴 찾음
        Menu menu = menuRepository.findById(dto.getId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 메뉴입니다. id = " + dto.getId()));

        // 파일이 null이 아닌 경우만 파일 수정
        if (dto.getFile() != null) {
            // 새로 업로드한 파일 UploadFile로 생성
            UploadFile newUploadFile = awsS3FileStore.storeFile(dto.getFile());

            // 기존 메뉴의 업로드 파일 찾음
            UploadFile file = menu.getFile();

            // 메뉴의 저장명, 업로드명 업데이트
            file.updateUploadedFileName(
                    newUploadFile.getStoredFileName(), newUploadFile.getUploadedFileName(), newUploadFile.getUploadImageUrl());
        }

        // 기존 메뉴 업데이트
        menu.updateWithoutImage(entity);

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

    public List<Menu> findByStoreIdCaseByOrderByType(Long storeId) {
        return menuRepository.findByStoreIdCaseByOrderByType(storeId);
    }
}