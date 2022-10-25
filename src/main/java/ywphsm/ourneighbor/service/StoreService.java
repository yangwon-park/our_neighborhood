package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.category.CategoryOfStore;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
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
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
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

    // 매장 등록
    @Transactional
    public Long save(StoreDTO.Add dto, List<Category> categoryList) {
        Store store = dto.toEntity();
        Member member = memberService.findById(dto.getMemberId());

        MemberOfStore memberOfStore = MemberOfStore.linkMemberOfStore(member, storeRepository.save(store));
        memberOfStore.updateMyStore(true);

        for (Category category : categoryList) {
            linkCategoryAndStore(category, store);
        }

        // default: OPEN
        store.updateStatus(StoreStatus.OPEN);

        memberOfStoreRepository.save(memberOfStore);

        return store.getId();
    }


    @Transactional
    public Long update(Long storeId, StoreDTO.Update dto, List<Long> categoryIdList) {

        Store findStore = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 매장입니다. id = " + storeId));

        // 먼저 카테고리를 업데이트
        List<CategoryOfStore> categoryOfStoreList = findStore.getCategoryOfStoreList();

        for (int i = 0; i < categoryOfStoreList.size(); i++) {
            Long categoryId = categoryIdList.get(i);

            Category category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 카테고리입니다. id = " + categoryId));
            categoryOfStoreList.get(i).updateCategory(category);
        }

        // 그 후, dto로 전달받은 수정된 정보를 별도로 업데이트 시킴
        findStore.update(dto.toEntity());

        return storeId;
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

    //찜 상태 업데이트
    @Transactional
    public void updateLike(boolean likeStatus, Long memberId, Long storeId) {
        Member member = memberService.findById(memberId);
        Store store = findById(storeId);

        if (likeStatus) {
            MemberOfStore memberOfStore = MemberOfStore.linkMemberOfStore(member, store);
            memberOfStore.updateStoreLike(true);
            memberOfStoreRepository.save(memberOfStore);
        } else {
            List<MemberOfStore> collect = member.getMemberOfStoreList().stream()
                    .filter(memberOfStore -> memberOfStore.getStore().getId().equals(storeId))
                    .collect(Collectors.toList());

            MemberOfStore memberOfStore = collect.get(0);
            memberOfStore.updateStoreLike(false);
            if (!memberOfStore.isMyStore()) {
                memberOfStoreRepository.delete(memberOfStore);
            }
        }
    }

    // 참고
    // https://wooody92.github.io/project/JPA%EC%99%80-MySQL%EB%A1%9C-%EC%9C%84%EC%B9%98-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%8B%A4%EB%A3%A8%EA%B8%B0/
    public List<Store> getTop5ByCategories(String categoryId, double lat, double lon) {

        double dist = 1.5;

        Location northEast = Distance.calculatePoint(lat, lon, dist, Direction.NORTHEAST.getAngle());
        Location southWest = Distance.calculatePoint(lat, lon, dist, Direction.SOUTHWEST.getAngle());

        double nex = northEast.getLat();
        double ney = northEast.getLon();
        double swx = southWest.getLat();
        double swy = southWest.getLon();

        // Native Query
        String pointFormat = String.format("'LINESTRING(%f %f, %f %f)'", nex, ney, swx, swy);

        return (List<Store>) em.createNativeQuery(
                        "select * " +
                                "from store as s " +
                                "join category_of_store as cs on cs.store_id = s.store_id " +
                                "where mbrcontains(" +
                                "ST_LineStringFromText(" + pointFormat + "), " +
                                "POINT(s.lat, s.lon)) " +
                                "and cs.category_id = :categoryId",
                        Store.class)
                .setParameter("categoryId", Long.parseLong(categoryId))
                .setMaxResults(5)
                .getResultList();
    }
}