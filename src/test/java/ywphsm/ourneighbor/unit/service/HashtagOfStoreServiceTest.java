package ywphsm.ourneighbor.unit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagOfStoreDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.hashtag.hashtagofstore.HashtagOfStoreRepository;
import ywphsm.ourneighbor.service.HashtagOfStoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.util.ReflectionTestUtils.*;

@ExtendWith(MockitoExtension.class)
public class HashtagOfStoreServiceTest {

    @Mock
    HashtagOfStoreRepository hashtagOfStoreRepository;

    @InjectMocks
    HashtagOfStoreService hashtagOfStoreService;

    @Test
    @DisplayName("매장에 등록된 해쉬태그를 등록 횟수를 기준으로 내림차순 정렬하여 조회")
    void should_FindHashtagAndCount_When_OrderByCountDescAndTop9() {
        // given
        Store store = Store.builder()
                .name("매장")
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        Long mockStoreId = 1L;

        setField(store, "id", mockStoreId);

        HashtagOfStoreDTO.WithCount withCountDto1 = HashtagOfStoreDTO.WithCount.builder()
                .hashtagName("해쉬태그1")
                .storeId(mockStoreId)
                .count(2L)
                .build();

        HashtagOfStoreDTO.WithCount withCountDto2 = HashtagOfStoreDTO.WithCount.builder()
                .hashtagName("해쉬태그2")
                .storeId(mockStoreId)
                .count(3L)
                .build();

        List<HashtagOfStoreDTO.WithCount> withCountList = new ArrayList<>();
        withCountList.add(withCountDto1);
        withCountList.add(withCountDto2);

        CustomComparator comp = new CustomComparator();
        withCountList.sort(comp);

        given(hashtagOfStoreRepository.findHashtagAndCountByStoreIdOrderByCountDescTop9(mockStoreId)).willReturn(withCountList);

        // when
        List<HashtagOfStoreDTO.WithCount> result = hashtagOfStoreService.findHashtagAndCountByStoreIdOrderByCountDescTop9(mockStoreId);


        // then
        assertThat(result).hasSize(2).containsExactly(withCountDto2, withCountDto1);                // dto1이 2개, dto2가 3개 => dto2가 먼저 나옴
        then(hashtagOfStoreRepository).should().findHashtagAndCountByStoreIdOrderByCountDescTop9(mockStoreId);
    }

    @Test
    @DisplayName("매장에 등록된 해쉬태그를 등록 횟수로 내림차순, 해쉬태그 이름을 기준으로 오름차순 정렬하여 조회")
    void should_FindHashtagAndCount_When_OrderByCountDescAndHashtagName() {
        // given
        Store store = Store.builder()
                .name("매장")
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        Long mockStoreId = 1L;

        setField(store, "id", mockStoreId);

        HashtagOfStoreDTO.WithCount withCountDto1 = HashtagOfStoreDTO.WithCount.builder()
                .hashtagName("가-해쉬태그")
                .storeId(mockStoreId)
                .count(2L)
                .build();

        HashtagOfStoreDTO.WithCount withCountDto2 = HashtagOfStoreDTO.WithCount.builder()
                .hashtagName("나-해쉬태그")
                .storeId(mockStoreId)
                .count(1L)
                .build();

        HashtagOfStoreDTO.WithCount withCountDto3 = HashtagOfStoreDTO.WithCount.builder()
                .hashtagName("나-해쉬태그")
                .storeId(mockStoreId)
                .count(3L)
                .build();

        List<HashtagOfStoreDTO.WithCount> withCountList = new ArrayList<>();
        withCountList.add(withCountDto1);
        withCountList.add(withCountDto2);
        withCountList.add(withCountDto3);

        CustomComparator comp = new CustomComparator();
        withCountList.sort(comp);

        given(hashtagOfStoreRepository.findHashtagAndCountByStoreIdOrderByCountDescOrderByHashtagName(mockStoreId)).willReturn(withCountList);

        // when
        List<HashtagOfStoreDTO.WithCount> result = hashtagOfStoreService.findHashtagAndCountByStoreIdOrderByCountDescOrderByHashtagName(mockStoreId);

        // then
        assertThat(result).hasSize(3).containsExactly(withCountDto3, withCountDto1, withCountDto2);
        then(hashtagOfStoreRepository).should().findHashtagAndCountByStoreIdOrderByCountDescOrderByHashtagName(mockStoreId);
    }

    /*
        정렬을 위해 생성한 Comparator 내부 클래스
     */
    static class CustomComparator implements Comparator<HashtagOfStoreDTO.WithCount> {
        @Override
        public int compare(HashtagOfStoreDTO.WithCount c1, HashtagOfStoreDTO.WithCount c2) {
            Long cnt1 = c1.getCount();
            Long cnt2 = c2.getCount();

            if (cnt1 > cnt2) {
                return -1;
            } else if (cnt1 < cnt2) {
                return 1;
            }

            return 0;
        }
    }
}



