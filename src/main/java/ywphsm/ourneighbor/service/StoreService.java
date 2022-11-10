package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.category.CategoryOfStore;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.file.AwsS3FileStore;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.MemberOfStore;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;
import ywphsm.ourneighbor.domain.store.distance.Direction;
import ywphsm.ourneighbor.domain.store.distance.Distance;
import ywphsm.ourneighbor.domain.store.distance.Location;
import ywphsm.ourneighbor.repository.category.CategoryRepository;
import ywphsm.ourneighbor.repository.member.MemberOfStoreRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ywphsm.ourneighbor.domain.category.CategoryOfStore.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true) // 데이터 변경 X
public class StoreService {

    private final EntityManager em;

    private final StoreRepository storeRepository;

    private final CategoryRepository categoryRepository;

    private final MemberOfStoreRepository memberOfStoreRepository;

    private final MemberService memberService;

    private final CategoryService categoryService;

    private final AwsS3FileStore awsS3FileStore;

    // 매장 등록
    @Transactional
    public Long save(StoreDTO.Add dto, List<Long> categoryIdList) {

        Store store = dto.toEntity();
        Member member = memberService.findById(dto.getMemberId());

        MemberOfStore memberOfStore = MemberOfStore.linkMemberOfStore(member, storeRepository.save(store));
        memberOfStore.updateMyStore(true);

        List<Category> categoryList = getNotNullCategoryList(categoryIdList);

        for (Category category : categoryList) {
            linkCategoryAndStore(category, store);
        }

        // default: OPEN
        store.updateStatus(StoreStatus.OPEN);

        memberOfStoreRepository.save(memberOfStore);

        return store.getId();
    }

    @Transactional
    public Long saveMainImage(Long storeId, MultipartFile file) throws IOException {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 매장입니다. id = " + storeId));

        UploadFile newUploadFile = awsS3FileStore.storeFile(file);

        newUploadFile.addStore(store);

        return storeId;
    }


    @Transactional
    public Long update(Long storeId, StoreDTO.Update dto, List<Long> categoryIdList) {

        Store findStore = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 매장입니다. id = " + storeId));

        // 먼저 카테고리를 업데이트
        List<CategoryOfStore> prevCategoryOfStoreList = findStore.getCategoryOfStoreList();

        List<Category> categoryList = getNotNullCategoryList(categoryIdList);
        
        // 카테고리는 무조건 1개 이상 존재해야 함
        if (prevCategoryOfStoreList != null) {
            if (prevCategoryOfStoreList.size() == categoryList.size()) {
                for (int i = 0; i < prevCategoryOfStoreList.size(); i++) {
                    prevCategoryOfStoreList.get(i).updateCategory(categoryList.get(i));
                }
            }

            if (prevCategoryOfStoreList.size() < categoryList.size()) {
                int i;

                for (i = 0; i < prevCategoryOfStoreList.size(); i++) {
                    prevCategoryOfStoreList.get(i).updateCategory(categoryList.get(i));
                }

                for (int j = i; j < categoryList.size(); j++) {
                    linkCategoryAndStore(categoryList.get(j), findStore);
                }
            }

            if (prevCategoryOfStoreList.size() > categoryList.size()) {
                int i;

                for (i = 0; i < categoryList.size(); i++) {
                    prevCategoryOfStoreList.get(i).updateCategory(categoryList.get(i));
                }

                for (int j = i; j < prevCategoryOfStoreList.size(); j++) {
                    categoryService.deleteByCategory(prevCategoryOfStoreList.get(j).getCategory());
                }
            }
        }

        // 그 후, dto로 전달받은 수정된 정보를 별도로 업데이트 시킴
        findStore.update(dto.toEntity());

        return storeId;
    }


    // 메인 이미지 업데이트
    @Transactional
    public Long updateMainImage(Long storeId, MultipartFile file) throws IOException {

        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 매장입니다. id = " + storeId));

        UploadFile uploadFile = awsS3FileStore.storeFile(file);

        if (store.getFile() != null) {
            UploadFile prevFile = store.getFile();

            prevFile.updateUploadedFileName(
                    uploadFile.getStoredFileName(), uploadFile.getUploadedFileName(), uploadFile.getUploadImageUrl()
            );
        }

        return storeId;
    }

    //찜 상태 업데이트
    @Transactional
    public void updateLike(boolean likeStatus, Long memberId, Long storeId) {
        Member member = memberService.findById(memberId);
        Store store = findById(storeId);

        List<MemberOfStore> collect = member.getMemberOfStoreList().stream()
                .filter(memberOfStore -> memberOfStore.getStore().getId().equals(storeId))
                .collect(Collectors.toList());

        if (likeStatus) {
            if (!collect.isEmpty()) {
                collect.get(0).updateStoreLike(true);
            } else {
                MemberOfStore memberOfStore = MemberOfStore.linkMemberOfStore(member, store);
                memberOfStore.updateStoreLike(true);
                memberOfStoreRepository.save(memberOfStore);
            }
        } else {
            MemberOfStore memberOfStore = collect.get(0);
            memberOfStore.updateStoreLike(false);
            if (!memberOfStore.isMyStore()) {
                memberOfStoreRepository.delete(memberOfStore);
            }
        }
    }

    @Transactional
    public Long delete(Long storeId) {

        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("해당 매장이 없습니다. id = " + storeId));

        List<MemberOfStore> memberOfStoreList = store.getMemberOfStoreList();
        memberOfStoreRepository.deleteAll(memberOfStoreList);
        storeRepository.delete(store);

        return storeId;
    }

    // 매장 하나 조회
    public Store findById(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id = " + storeId));

        store.autoUpdateStatus(store.getOffDays(), store.getBusinessTime());

        return store;
    }

    // 전체 매장 조회
    public List<Store> findStores() {
        return storeRepository.findAll();
    }

    // 검색어 포함 매장명 조회
    public List<Store> searchByKeyword(String keyword) {
        List<Store> stores = storeRepository.searchByKeyword(keyword);

        for (Store store : stores) {
            store.autoUpdateStatus(store.getOffDays(), store.getBusinessTime());
        }

        return stores;
    }

    public List<Store> searchByCategory(Long categoryId) {
        return storeRepository.searchByCategory(categoryId);
    }


    // 참고
    // https://wooody92.github.io/project/JPA%EC%99%80-MySQL%EB%A1%9C-%EC%9C%84%EC%B9%98-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%8B%A4%EB%A3%A8%EA%B8%B0/
    public List<Store> getTop5ByCategories(Long categoryId, double dist, double lat, double lon) throws ParseException {
        return storeRepository.getTop5ByCategories(categoryId, dist, lat, lon);
    }

    public List<String> getTop5ImageByCategories(Long categoryId, double dist, double lat, double lon) throws ParseException {
        List<Store> top5 = storeRepository.getTop5ByCategories(categoryId, dist, lat, lon);
        List<String> top5UrlList = new ArrayList<>();

        for (Store store : top5) {
            if (store.getFile() != null) {
                String url = store.getFile().getUploadImageUrl();

                top5UrlList.add(url);
            }
        }

        return top5UrlList;
    }


    //매장주인이 맞는지 체크
    public boolean OwnerCheck(Member member, Long storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store != null) {
            List<MemberOfStore> memberOfStoreList = store.getMemberOfStoreList();

            for (MemberOfStore memberOfStore : memberOfStoreList) {
                Long id = memberOfStore.getMember().getId();
                if (member.getId().equals(id) && memberOfStore.isMyStore()) {
                    return true;
                }
            }
        }

        return false;
    }

    // 대 중 소 분류 모두가 들어오지 않을 수도 있으므로
    // null이 아닌 categoryId만 리스트로 반환
    private List<Category> getNotNullCategoryList(List<Long> categoryIdList) {
        List<Long> collect = categoryIdList.stream().filter(Objects::nonNull)
                .collect(Collectors.toList());

        return collect.stream().map(categoryService::findById)
                .collect(Collectors.toList());
    }
}