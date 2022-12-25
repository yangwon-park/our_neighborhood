package ywphsm.ourneighbor.unit.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ywphsm.ourneighbor.domain.category.Category;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.util.ReflectionTestUtils.*;
import static ywphsm.ourneighbor.domain.member.MemberOfStore.linkMemberOfStore;

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
    void should_SaveAStore() {
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
//
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
}
