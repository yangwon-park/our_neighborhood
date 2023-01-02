package ywphsm.ourneighbor.unit.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.category.CategoryOfStore;
import ywphsm.ourneighbor.domain.dto.store.StoreDTO;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.MemberOfStore;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;
import ywphsm.ourneighbor.domain.store.days.Days;
import ywphsm.ourneighbor.domain.store.days.DaysType;
import ywphsm.ourneighbor.repository.category.CategoryRepository;
import ywphsm.ourneighbor.repository.member.MemberOfStoreRepository;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;
import ywphsm.ourneighbor.repository.store.days.DaysRepository;
import ywphsm.ourneighbor.service.store.StoreService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.util.ReflectionTestUtils.*;
import static ywphsm.ourneighbor.domain.category.CategoryOfStore.linkCategoryAndStore;
import static ywphsm.ourneighbor.domain.member.MemberOfStore.linkMemberOfStore;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberOfStoreRepository memberOfStoreRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private DaysRepository daysRepository;

    @InjectMocks
    private StoreService storeService;

    @BeforeEach
    void beforeEach() {

    }

    @Test
    @DisplayName("매장 저장")
    void should_SaveStore() {
        // given
        Member member = Member.builder()
                .username("회원1")
                .build();

        Category category = Category.builder()
                .name("카테고리1")
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Days days = Days.builder()
                .type(DaysType.SUN)
                .build();

        StoreDTO.Add dto = StoreDTO.Add.builder()
                .name("매장1")
                .roadAddr("테스트 로")
                .numberAddr("111111로")
                .zipcode("48017")
                .detail("상세")
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .openingTime(LocalTime.now())
                .openingTime(LocalTime.now().plusHours(1))
                .breakStart(null)
                .breakEnd(null)
                .build();

        Long mockId = 1L;

        setField(dto, "memberId", mockId);

        Store entity = dto.toEntity();

        List<Long> categoryIdList = new ArrayList<>();
        categoryIdList.add(mockId);

        List<Long> daysIdList = new ArrayList<>();
        daysIdList.add(mockId);

        MemberOfStore memberOfStore = linkMemberOfStore(member, entity);

        given(memberRepository.findById(mockId)).willReturn(Optional.of(member));
        given(storeRepository.save(any())).willReturn(entity);
        given(memberOfStoreRepository.save(any())).willReturn(memberOfStore);
        given(categoryRepository.findById(mockId)).willReturn(Optional.ofNullable(category));
        given(daysRepository.findById(mockId)).willReturn(Optional.ofNullable(days));

        // when
        storeService.save(dto, categoryIdList, daysIdList);

        // then
        then(memberRepository).should().findById(mockId);
        then(storeRepository).should(times(1)).save(any());
        then(memberOfStoreRepository).should(times(1)).save(any());
        then(categoryRepository).should().findById(mockId);
        then(daysRepository).should().findById(mockId);
        assertThat(entity.getName()).isEqualTo("매장1");
    }

    @Test
    @DisplayName("매장 수정 - 단순 매장 정보만 수정한 경우")
    void should_UpdateStore() {
        // given
        Long mockId = 1L;

        Category category = Category.builder()
                .name("카테고리1")
                .depth(1L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Days days = Days.builder()
                .type(DaysType.SUN)
                .build();

        Store store = Store.builder()
                .name("매장")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .categoryOfStoreList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .daysOfStoreList(new ArrayList<>())
                .build();
        
        linkCategoryAndStore(category, store);              // 기존 카테고리와 연결

        StoreDTO.Update dto = StoreDTO.Update.builder()
                .name("업데이트된 매장")
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .build();

        setField(category, "id", mockId);
        setField(store, "id", mockId);
        setField(days, "id", mockId);

        List<Long> categoryIdList = new ArrayList<>();
        categoryIdList.add(mockId);

        List<Long> daysIdList = new ArrayList<>();
        daysIdList.add(mockId);

        given(storeRepository.findById(mockId)).willReturn(Optional.of(store));
        given(categoryRepository.findById(mockId)).willReturn(Optional.of(category));
        given(daysRepository.findById(mockId)).willReturn(Optional.of(days));

        // when
        Long updateStoreId = storeService.update(mockId, dto, categoryIdList, daysIdList);
        Store updateStore = storeService.findById(updateStoreId);

        // then
        then(storeRepository).should(times(2)).findById(mockId);
        then(categoryRepository).should().findById(mockId);
        then(daysRepository).should().findById(mockId);
        assertThat(updateStoreId).isEqualTo(mockId);
        assertThat(updateStore.getName()).isEqualTo(dto.getName());
    }

    @Test
    @DisplayName("매장 수정 - 같은 레벨의 카테고리만 수정한 경우")
    void should_UpdateStore_When_UpdateSameLevelCategory() {
        // given
        Category prevCategory = Category.builder()
                .name("기존 카테고리")
                .depth(1L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category newCategory = Category.builder()
                .name("새롭게 수정한 카테고리")
                .depth(1L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Days days = Days.builder()
                .type(DaysType.SUN)
                .build();

        Store store = Store.builder()
                .name("매장")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .categoryOfStoreList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .daysOfStoreList(new ArrayList<>())
                .build();

        StoreDTO.Update dto = StoreDTO.Update.builder()
                .name("업데이트된 매장")
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .build();

        Long mockId = 1L;
        Long mockCategoryId1 = 1L;
        Long mockCategoryId2 = 2L;

        setField(prevCategory, "id", mockCategoryId1);
        setField(newCategory, "id", mockCategoryId2);
        setField(store, "id", mockId);
        setField(days, "id", mockId);

        linkCategoryAndStore(prevCategory, store);              // 기존 카테고리와 연결

        List<Long> categoryIdList = new ArrayList<>();
        categoryIdList.add(mockCategoryId2);

        List<Long> daysIdList = new ArrayList<>();
        daysIdList.add(mockId);

        given(storeRepository.findById(mockId)).willReturn(Optional.of(store));
        given(categoryRepository.findById(mockCategoryId2)).willReturn(Optional.of(newCategory));
        given(daysRepository.findById(mockId)).willReturn(Optional.of(days));

        // when
        Long updateStoreId = storeService.update(mockId, dto, categoryIdList, daysIdList);
        Store updateStore = storeService.findById(updateStoreId);

        // then
        then(storeRepository).should(times(2)).findById(mockId);
        then(categoryRepository).should().findById(mockCategoryId2);
        then(daysRepository).should().findById(mockId);
        assertThat(updateStoreId).isEqualTo(mockId);
        assertThat(updateStore.getCategoryOfStoreList().size()).isEqualTo(1);
        assertThat(updateStore.getCategoryOfStoreList().get(0).getCategory().getName()).isEqualTo(newCategory.getName());
    }

    @Test
    @DisplayName("매장 수정 - 하위 카테고리를 추가한 경우")
    void should_UpdateStore_When_AddSubCategory() {
        Category prevCategory = Category.builder()
                .name("기존 카테고리")
                .depth(1L)
                .categoryOfStoreList(new ArrayList<>())
                .build();

        Category newCategory = Category.builder()
                .name("새롭게 추가한 하위 카테고리")
                .depth(2L)
                .categoryOfStoreList(new ArrayList<>())
                .parent(prevCategory)
                .build();

        Days days = Days.builder()
                .type(DaysType.SUN)
                .build();

        Store store = Store.builder()
                .name("매장")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .categoryOfStoreList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .daysOfStoreList(new ArrayList<>())
                .build();

        StoreDTO.Update dto = StoreDTO.Update.builder()
                .name("업데이트된 매장")
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .build();

        Long mockId = 1L;
        Long mockCategoryId1 = 1L;
        Long mockCategoryId2 = 2L;

        setField(prevCategory, "id", mockCategoryId1);
        setField(newCategory, "id", mockCategoryId2);
        setField(store, "id", mockId);
        setField(days, "id", mockId);

        linkCategoryAndStore(prevCategory, store);              // 기존 카테고리와 연결

        List<Long> categoryIdList = new ArrayList<>();
        categoryIdList.add(mockCategoryId1);
        categoryIdList.add(mockCategoryId2);

        List<Long> daysIdList = new ArrayList<>();
        daysIdList.add(mockId);

        given(storeRepository.findById(mockId)).willReturn(Optional.of(store));
        given(categoryRepository.findById(mockCategoryId1)).willReturn(Optional.of(prevCategory));
        given(categoryRepository.findById(mockCategoryId2)).willReturn(Optional.of(newCategory));
        given(daysRepository.findById(mockId)).willReturn(Optional.of(days));

        // when
        Long updateStoreId = storeService.update(mockId, dto, categoryIdList, daysIdList);
        Store updateStore = storeService.findById(updateStoreId);

        // then
        then(storeRepository).should(times(2)).findById(mockId);
        then(categoryRepository).should().findById(mockCategoryId1);
        then(categoryRepository).should().findById(mockCategoryId2);
        then(daysRepository).should().findById(mockId);
        assertThat(updateStoreId).isEqualTo(mockId);
        assertThat(updateStore.getCategoryOfStoreList().size()).isEqualTo(2);
        assertThat(updateStore.getCategoryOfStoreList().get(1).getCategory().getName()).isEqualTo(newCategory.getName());
    }
}
