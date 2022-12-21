package ywphsm.ourneighbor.service.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.geolatte.geom.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.category.CategoryOfStore;
import ywphsm.ourneighbor.domain.dto.store.StoreDTO;
import ywphsm.ourneighbor.domain.file.AwsS3FileStore;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.MemberOfStore;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;
import ywphsm.ourneighbor.domain.store.days.Days;
import ywphsm.ourneighbor.domain.store.days.DaysOfStore;
import ywphsm.ourneighbor.domain.store.distance.Location;
import ywphsm.ourneighbor.repository.category.CategoryRepository;
import ywphsm.ourneighbor.repository.member.MemberOfStoreRepository;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;
import ywphsm.ourneighbor.repository.store.days.DaysRepository;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;
import ywphsm.ourneighbor.service.MemberService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.builder.DSL.ring;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.*;
import static ywphsm.ourneighbor.domain.category.CategoryOfStore.*;
import static ywphsm.ourneighbor.domain.file.FileUtil.getResizedMultipartFile;
import static ywphsm.ourneighbor.domain.member.MemberOfStore.*;
import static ywphsm.ourneighbor.domain.store.days.DaysOfStore.linkDaysAndStore;
import static ywphsm.ourneighbor.domain.store.distance.Direction.*;
import static ywphsm.ourneighbor.domain.store.distance.Distance.calculatePoint;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    private final CategoryRepository categoryRepository;

    private final DaysRepository daysRepository;

    private final AwsS3FileStore awsS3FileStore;

    private final MemberOfStoreRepository memberOfStoreRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public Long save(StoreDTO.Add dto, List<Long> categoryIdList, List<Long> daysIdList) {
        Store store = dto.toEntity();
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + dto.getMemberId()));

        Point<G2D> point = point(WGS84, g(dto.getLon(), dto.getLat()));
        store.addPoint(point);

        MemberOfStore memberOfStore = linkMemberOfStore(member, storeRepository.save(store));
        memberOfStore.updateMyStore(true);
        memberOfStoreRepository.save(memberOfStore);

        List<Category> categoryList = getNotNullCategoryList(categoryIdList);

        for (Category category : categoryList) {
            linkCategoryAndStore(category, store);
        }

        if (!(daysIdList == null)) {
            List<Days> daysList = daysIdList.stream().map(id -> daysRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 요일값입니다."))).collect(Collectors.toList());

            for (Days days : daysList) {
                linkDaysAndStore(days, store);
            }
        }

        store.updateStatus(StoreStatus.OPEN);           // default: OPEN

        return store.getId();
    }

    @Transactional
    public Long update(Long storeId, StoreDTO.Update dto,
                       List<Long> categoryIdList, List<Long> daysIdList) {
        Store findStore = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 매장입니다. id = " + storeId));
        Point<G2D> point = point(WGS84, g(dto.getLon(), dto.getLat()));
        dto.setPoint(point);

        /*
            먼저 카테고리를 업데이트
         */
        List<CategoryOfStore> prevCategoryOfStoreList = findStore.getCategoryOfStoreList();

        List<Category> categoryList = getNotNullCategoryList(categoryIdList);

        /*
            카테고리는 무조건 1개 이상 존재해야 함
         */
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
                    categoryRepository.deleteByCategoryLinkedCategoryOfStore(prevCategoryOfStoreList.get(j).getCategory());
                }
            }
        }

        /*
            다음 days 업데이트
         */
        List<DaysOfStore> prevDaysOfStoreList = findStore.getDaysOfStoreList();

        /*
            체크박스가 하나도 체크돼있지 않은데 기존의 휴무는 있는 경우
            휴무가 존재했으나 없앴다 => 매장에 등록된 휴무 데이터를 다 삭제함
         */
        if (daysIdList == null) {
            if (prevDaysOfStoreList != null) {
                daysRepository.deleteByStoreId(findStore.getId());
            }
        } else {
            List<Days> daysList = daysIdList.stream().map(id -> daysRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 요일값입니다."))).collect(Collectors.toList());

            if (prevDaysOfStoreList.size() == daysList.size()) {
                for (int i = 0; i < prevDaysOfStoreList.size(); i++) {
                    prevDaysOfStoreList.get(i).updateDays(daysList.get(i));
                }
            }

            if (prevDaysOfStoreList.size() < daysList.size()) {
                int i;

                for (i = 0; i < prevDaysOfStoreList.size(); i++) {
                    prevDaysOfStoreList.get(i).updateDays(daysList.get(i));
                }

                for (int j = i; j < daysList.size(); j++) {
                    linkDaysAndStore(daysList.get(j), findStore);
                }
            }

            // 현재 여기 버그발생
            if (prevDaysOfStoreList.size() > daysList.size()) {
                int i;

                for (i = 0; i < daysList.size(); i++) {
                    prevDaysOfStoreList.get(i).updateDays(daysList.get(i));
                }

                for (int j = i; j < prevDaysOfStoreList.size(); j++) {
                    daysRepository.deleteByDaysIdLinkedDaysOfStore(prevDaysOfStoreList.get(j).getDays().getId());
                }
            }
        }

        /*
            그 후, dto로 전달받은 수정된 정보를 별도로 업데이트 시킴
         */
        findStore.update(dto.toEntity());

        return storeId;
    }

    @Transactional
    public Long delete(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("해당 매장이 없습니다. id = " + storeId));

        List<MemberOfStore> memberOfStoreList = store.getMemberOfStoreList();
        memberOfStoreRepository.deleteAll(memberOfStoreList);

        if ((store.getFile().getStoredFileName() != null)) {
            awsS3FileStore.deleteFile(store.getFile().getStoredFileName());
        }

        storeRepository.delete(store);

        return storeId;
    }

    @Transactional
    public Long saveMainImage(Long storeId, MultipartFile file) throws IOException {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 매장입니다. id = " + storeId));

        UploadFile newUploadFile = awsS3FileStore.storeFile(getResizedMultipartFile(file, file.getOriginalFilename()));

        newUploadFile.addStore(store);

        return storeId;
    }

    @Transactional
    public Long updateMainImage(Long storeId, MultipartFile file) throws IOException {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 매장입니다. id = " + storeId));

        awsS3FileStore.deleteFile(store.getFile().getStoredFileName());

        UploadFile newUploadFile = awsS3FileStore.storeFile(getResizedMultipartFile(file, file.getOriginalFilename()));

        if (store.getFile() != null) {
            UploadFile prevFile = store.getFile();

            prevFile.updateUploadedFileName(
                    newUploadFile.getStoredFileName(), newUploadFile.getUploadedFileName(), newUploadFile.getUploadImageUrl()
            );
        }

        return storeId;
    }

    @Transactional
    public String updateLike(boolean likeStatus, Long memberId, Long storeId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + memberId));
        Store store = findById(storeId);

        List<MemberOfStore> collect = member.getMemberOfStoreList().stream()
                .filter(memberOfStore -> memberOfStore.getStore().getId().equals(storeId))
                .collect(Collectors.toList());

        if (likeStatus) {
            if (!collect.isEmpty()) {
                collect.get(0).updateStoreLike(true);
                return "가게가 찜 등록이 되었습니다.";
            }

            MemberOfStore memberOfStore = linkMemberOfStore(member, store);
            memberOfStore.updateStoreLike(true);
            memberOfStoreRepository.save(memberOfStore);
            return "가게가 찜 등록이 되었습니다.";
        }

        MemberOfStore memberOfStore = collect.get(0);
        if (!memberOfStore.isMyStore()) {
            memberOfStoreRepository.delete(memberOfStore);
            return "가게가 찜 등록이 취소되었습니다.";
        }

        memberOfStore.updateStoreLike(false);
        return "가게가 찜 등록이 취소되었습니다.";
    }

    public Store findById(Long storeId) {
        return storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id = " + storeId));
    }

    public List<Store> findAllStoresJoinUploadFileFetchJoin() {
        return storeRepository.findAllStoresJoinUploadFileFetchJoin();
    }

    public List<Store> searchByKeyword(String keyword) {
        return storeRepository.searchByKeyword(keyword);
    }

    public List<Store> searchByCategory(Long categoryId) {
        return storeRepository.searchByCategory(categoryId);
    }

    public Slice<SimpleSearchStoreDTO> searchByHashtag(List<Long> hashtagIdList, int page, double lat,
                                                       double lon, double dist) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        return storeRepository.searchByHashtag(hashtagIdList, getPolygon(lat, lon, dist), pageRequest);
    }

    public List<SimpleSearchStoreDTO> searchTop7Random(double lat, double lon, double dist) {
        final int n = 7;

        Polygon<G2D> polygon = getPolygon(lat, lon, dist);

        Long count = storeRepository.countStoreInPolygon(polygon);

        int idx = count % n != 0
                    ? (int) (Math.random() * (count / n + 1))
                    : (int) (Math.random() * (count / n));

        PageRequest pageRequest = PageRequest.of(idx, n);

        return storeRepository.searchTop7Random(polygon, pageRequest);
    }

    public List<Store> searchTopNByCategories(Long categoryId, double dist, double lat, double lon) {
        return storeRepository.searchTopNByCategories(getPolygon(lat, lon, dist), categoryId);
    }

    public List<String> getTopNImageByCategories(Long categoryId, double dist, double lat, double lon) {
        List<Store> topStoreList = storeRepository.searchTopNByCategories(getPolygon(lat, lon, dist), categoryId);

        return topStoreList.stream()
                .filter(store -> store.getFile() != null)
                .map(store -> store.getFile().getUploadImageUrl())
                .collect(Collectors.toList());
    }


    /*
        매장주인이 맞는지 체크
     */
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

    @Transactional
    public String addStoreOwner(String userId, Long storeId) {
        Member findMember = memberRepository.findByUserId(userId).orElse(null);
        if (findMember == null) {
            return "존재하지 않는 아이디 입니다";
        }
        if (findMember.getRole() == Role.USER) {
            return "가게를 관리할 권한이 없는 아이디입니다";
        }

        Store findStore = findById(storeId);
        List<MemberOfStore> DuplicateCheck = findStore.getMemberOfStoreList().stream()
                .filter(memberOfStore -> memberOfStore.getMember().equals(findMember))
                .collect(Collectors.toList());

        if (!DuplicateCheck.isEmpty()) {
            long OwnerCount = DuplicateCheck.stream()
                    .filter(MemberOfStore::isMyStore)
                    .count();

            long likeCount = DuplicateCheck.stream()
                    .filter(MemberOfStore::isStoreLike)
                    .count();
            log.info("likeCount={}", likeCount);
            if (OwnerCount > 0) {
                return "이미 등록된 관리자 입니다.";
            }
            if (likeCount > 0) {
                DuplicateCheck.get(0).updateMyStore(true);
                return "성공";
            }
        }
        MemberOfStore memberOfStore = MemberOfStore.linkMemberOfStore(findMember, findStore);
        memberOfStore.updateMyStore(true);
        memberOfStoreRepository.save(memberOfStore);

        return "성공";
    }

    @Transactional
    public String deleteStoreOwner(Long memberId, Long storeId) {
        try {
            Member findMember = memberRepository.findById(memberId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + memberId));
            Store findStore = findById(storeId);
            List<MemberOfStore> DuplicateCheck = findStore.getMemberOfStoreList().stream()
                    .filter(memberOfStore -> memberOfStore.getMember().equals(findMember))
                    .collect(Collectors.toList());

            if (!DuplicateCheck.isEmpty()) {
                long likeCount = DuplicateCheck.stream()
                        .filter(MemberOfStore::isStoreLike)
                        .count();
                if (likeCount > 0) {
                    DuplicateCheck.get(0).updateMyStore(false);
                    return "성공";
                }
                MemberOfStore memberOfStore = DuplicateCheck.get(0);
                memberOfStoreRepository.delete(memberOfStore);
                return "성공";
            }

            return "이미 삭제된 관리자입니다";

        } catch (IllegalArgumentException e) {
            return "존재하지 않는 아이디 입니다";
        }
    }


    /*
        대 중 소 분류 모두가 들어오지 않을 수도 있으므로
        null이 아닌 categoryId만 리스트로 반환
     */
    private List<Category> getNotNullCategoryList(List<Long> categoryIdList) {
        List<Long> collect = categoryIdList.stream().filter(Objects::nonNull)
                .collect(Collectors.toList());

        return collect.stream().map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다 id = " +  id)))
                .collect(Collectors.toList());
    }

    /*
        Polygon 생성 메소드
     */
    private Polygon<G2D> getPolygon(double lat, double lon, double dist) {
        double sqrt = Math.sqrt(2);
        double toCorner = dist * sqrt;          // 빗변의 길이 (직각 이등변 삼각형의 성질)

        Location northEast = calculatePoint(lat, lon, toCorner, NORTHEAST.getAngle());
        Location northWest = calculatePoint(lat, lon, toCorner, NORTHWEST.getAngle());
        Location southWest = calculatePoint(lat, lon, toCorner, SOUTHWEST.getAngle());
        Location southEast = calculatePoint(lat, lon, toCorner, SOUTHEAST.getAngle());

        double nex = northEast.getLon();
        double ney = northEast.getLat();

        double nwx = northWest.getLon();
        double nwy = northWest.getLat();

        double swx = southWest.getLon();
        double swy = southWest.getLat();

        double sex = southEast.getLon();
        double sey = southEast.getLat();

        return polygon(WGS84, ring(g(nex, ney),
                g(nwx, nwy), g(swx, swy), g(sex, sey), g(nex, ney)));
    }
}
