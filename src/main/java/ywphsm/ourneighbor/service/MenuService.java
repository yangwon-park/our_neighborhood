package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.file.AwsS3FileStore;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.file.FileStore;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.hashtag.HashtagRepository;
import ywphsm.ourneighbor.repository.hashtag.hashtagofmenu.HashtagOfMenuRepository;
import ywphsm.ourneighbor.repository.menu.MenuRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static ywphsm.ourneighbor.domain.file.FileUtil.*;
import static ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu.linkHashtagAndMenu;
import static ywphsm.ourneighbor.domain.hashtag.HashtagUtil.*;

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
                () -> new IllegalArgumentException("존재하지 않는 매장입니다. id = " + dto.getStoreId()));

        UploadFile newUploadFile = checkMenuTypeForResizing(dto.getType(), dto.getFile());

        Menu menu = dto.toEntity(linkedStore);
        newUploadFile.addMenu(menu);
        linkedStore.addMenu(menu);

        Menu savedMenu = menuRepository.save(menu);

        // 해쉬태그 저장 로직
        if (!dto.getHashtag().isEmpty()) {
            List<String> hashtagNameList = getHashtagNameList(dto.getHashtag());

            saveHashtagLinkedMenu(savedMenu, hashtagNameList);
        }

        return savedMenu.getId();
    }

    // 메뉴 수정
    @Transactional
    public Long update(Long storeId, MenuDTO.Update dto) throws IOException, ParseException {

        // 전달받은 dto => Entity 변환
        Menu entity = dto.toEntity();

        // 수정하고자 하는 메뉴 찾음
        Menu menu = menuRepository.findById(dto.getId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 메뉴입니다. id = " + dto.getId()));

        // 메뉴에 있던 기존 해쉬태그를 불러옴
        List<String> previousHashtagName = menu.getHashtagOfMenuList().stream()
                .map(hashtagOfMenu -> hashtagOfMenu.getHashtag().getName()).collect(Collectors.toList());

        // 새로 저장할 해쉬태그
        String hashtagJson = dto.getHashtag();

        // 기존 메뉴의 해쉬태그가 0개가 아닌데 dto로 들어온 해쉬태그 값이 0개인 경우
        //      => 해쉬태그를 모두 삭제한 경우
        // 하나 하나 삭제하기보단 걍 통째로 지우는게 좋을듯
        // 실제로 이럴 경우는 잘 없다고 봄
        if (hashtagJson != null) {
            if (previousHashtagName.size() != 0 && hashtagJson.isEmpty()) {
                hashtagOfMenuRepository.deleteByMenu(menu);
            }

            // 해쉬태그에 새로운 값을 추가한 경우
            if (!(hashtagJson.isEmpty())) {
                List<String> hashtagNameList = getHashtagNameList(hashtagJson);

                // 기존 해쉬태그가 없는 경우 => 그냥 처음 저장하는 과정과 동일
                if (previousHashtagName.size() == 0) {
                    saveHashtagLinkedMenu(menu, hashtagNameList);
                } else {
                    updateAndDeleteHashtagLinkedMenu(menu, previousHashtagName, hashtagNameList);
                }
            }
        }

        // 파일이 null이 아닌 경우만 파일 수정
        if (dto.getFile() != null) {
            UploadFile newUploadFile = checkMenuTypeForResizing(dto.getType(), dto.getFile());

            // 기존 메뉴의 업로드 파일 찾음
            UploadFile prevFile = menu.getFile();

            // 메뉴의 저장명, 업로드명 업데이트
            prevFile.updateUploadedFileName(
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

    public List<Menu> findByStoreIdWithoutTypeMenuCaseByOrderByType(Long storeId) {
        return menuRepository.findByStoreIdWithoutTypeMenuCaseByOrderByType(storeId);
    }

    // hashtag save 로직
    private void saveHashtagLinkedMenu(Menu menu, List<String> hashtagNameList) {
        for (String name : hashtagNameList) {
            boolean duplicateCheck = hashtagRepository.existsByName(name);

            Hashtag newHashtag;

            if (!duplicateCheck) {
                HashtagDTO hashtagDTO = HashtagDTO.builder()
                        .name(name)
                        .build();

                newHashtag = hashtagRepository.save(hashtagDTO.toEntity());
            } else {
                newHashtag = hashtagRepository.findByName(name);
            }

            linkHashtagAndMenu(newHashtag, menu);
        }
    }

    // 해쉬태그 update, delete 로직
    private void updateAndDeleteHashtagLinkedMenu(Menu menu, List<String> previousHashtagName, List<String> hashtagNameList) {
        for (String name : hashtagNameList) {
            boolean duplicateCheck = hashtagRepository.existsByName(name);

            Hashtag hashtag;

            // 기존에 등록돼있던 해쉬태그인 경우
            if (duplicateCheck) {
                hashtag = hashtagRepository.findByName(name);

                Boolean duplicateHashtagOfStoreCheck = hashtagOfMenuRepository.existsByHashtagAndMenu(hashtag, menu);

                // 기존에 연관관계가 설정되지 않은 해쉬태그 - 메뉴인 경우
                if (!duplicateHashtagOfStoreCheck) {
                    linkHashtagAndMenu(hashtag, menu);
                }

                // 수정 중, 완전히 새로운 해쉬태그를 만들 경우
            } else {
                HashtagDTO hashtagDTO = HashtagDTO.builder()
                        .name(name)
                        .build();

                hashtag = hashtagRepository.save(hashtagDTO.toEntity());
                linkHashtagAndMenu(hashtag, menu);
            }
        }

        // 기존의 해쉬태그 중, 새로운 해쉬태그에 포함되지 않는 경우
        //      => 해쉬태그를 삭제한 경우
        for (String prevName : previousHashtagName) {
            boolean containsCheck = hashtagNameList.contains(prevName);

            if (!(containsCheck)) {
                hashtagOfMenuRepository.deleteByHashtag(hashtagRepository.findByName(prevName));
            }
        }
    }

    public List<String> findMenuImg(Long storeId) {
        return menuRepository.findMenuImg(storeId);
    }
    
    /*
        MenuType != 메뉴판인 경우에만 리사이징 적용
     */
    private UploadFile checkMenuTypeForResizing(MenuType type, MultipartFile file) throws IOException {
        UploadFile newUploadFile;

        if (!type.equals(MenuType.MENU)) {
            MultipartFile resizedMultipartFile = getResizedMultipartFile(file, file.getOriginalFilename());
            newUploadFile = awsS3FileStore.storeFile(resizedMultipartFile);
        } else {
            newUploadFile = awsS3FileStore.storeFile(file);
        }

        return newUploadFile;
    }
}