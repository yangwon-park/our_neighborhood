package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.file.AwsS3FileStore;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
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
import java.util.stream.Collectors;

import static ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu.linkHashtagAndMenu;

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

        String hashtagJson = dto.getHashtag();

        // 해쉬태그 저장 로직
        if (!hashtagJson.isEmpty()) {
            Menu savedMenu = menuRepository.findById(savedMenuId).orElseThrow(
                    () -> new IllegalArgumentException("해당 메뉴가 없습니다. id = " + savedMenuId));

            List<String> hashtagNameList = getHashtagNameList(hashtagJson);

            saveHashtag(savedMenu, hashtagNameList);
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

        // 메뉴에 있던 기존 해쉬태그를 불러옴
        List<String> previousHashtagName = menu.getHashtagOfMenuList().stream()
                .map(hashtagOfMenu -> hashtagOfMenu.getHashtag().getName()).collect(Collectors.toList());

        // 새로 저장할 해쉬태그
        String hashtagJson = dto.getHashtag();

        // 기존 메뉴의 해쉬태그가 0개가 아닌데 dto로 들어온 해쉬태그 값이 0개인 경우
        //      => 해쉬태그를 모두 삭제한 경우
        // 하나 하나 삭제하기보단 걍 통째로 지우는게 좋을듯
        // 실제로 이럴 경우는 잘 없다고 봄
        if (previousHashtagName.size() != 0 && hashtagJson.isEmpty()) {
            log.info("해쉬태그 모두 삭제");
            hashtagOfMenuRepository.deleteByMenu(menu);
        }

        // 해쉬태그에 새로운 값을 추가한 경우
        if (!(hashtagJson.isEmpty())) {
            List<String> newHashtagNameList = getHashtagNameList(hashtagJson);

            // 기존 해쉬태그가 없는 경우 => 그냥 처음 저장하는 과정과 동일
            if (previousHashtagName.size() == 0) {
                saveHashtag(menu, newHashtagNameList);
            } else {
                updateAndDeleteHashtag(menu, previousHashtagName, newHashtagNameList);
            }
        }


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

    // json을 hashtagNameList로 변환해주는 로직
    private static List<String> getHashtagNameList(String hashtagJson) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(hashtagJson);

        List<String> hashtagNameList = new ArrayList<>();

        for (Object object : array) {
            JSONObject jsonObject = (JSONObject) object;
            String value = jsonObject.get("value").toString();

            hashtagNameList.add(value);
        }

        return hashtagNameList;
    }

    // hashtag save 로직
    private void saveHashtag(Menu menu, List<String> hashtagNameList) {
        for (String name : hashtagNameList) {
            HashtagDTO hashtagDTO = HashtagDTO.builder()
                    .name(name)
                    .build();

            boolean duplicateCheck = hashtagRepository.existsByName(name);

            Hashtag newHashtag;

            if (duplicateCheck) {
                newHashtag = hashtagRepository.findByName(name);
            } else {
                newHashtag = hashtagRepository.save(hashtagDTO.toEntity());
            }

            linkHashtagAndMenu(newHashtag, menu);
        }
    }

    // 해쉬태그 update, delete 로직
    private void updateAndDeleteHashtag(Menu menu, List<String> previousHashtagName, List<String> newHashtagNameList) {
        for (String name : newHashtagNameList) {
            boolean duplicateHashtagCheck = hashtagRepository.existsByName(name);

            Hashtag hashtag;

            // 기존에 등록돼있던 해쉬태그인 경우
            if (duplicateHashtagCheck) {
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
            boolean containsCheck = newHashtagNameList.contains(prevName);

            if (!(containsCheck)) {
                hashtagOfMenuRepository.deleteByHashtag(hashtagRepository.findByName(prevName));
            }
        }
    }
}