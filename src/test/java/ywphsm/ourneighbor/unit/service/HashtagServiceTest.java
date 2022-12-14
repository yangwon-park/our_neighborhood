package ywphsm.ourneighbor.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.hashtag.HashtagRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;
import ywphsm.ourneighbor.service.HashtagService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.util.ReflectionTestUtils.*;

@ExtendWith(MockitoExtension.class)
public class HashtagServiceTest {

    @Mock
    HashtagRepository hashtagRepository;

    @Mock
    StoreRepository storeRepository;

    @InjectMocks
    HashtagService hashtagService;

    @BeforeEach
    void beforeEach() {

    }

    @Test
    @DisplayName("해쉬태그 저장")
    void should_SaveHashtag() {
        // given
        HashtagDTO dto = HashtagDTO.builder()
                .name("해쉬태그")
                .build();

        given(hashtagRepository.save(any())).willReturn(dto.toEntity());

        // when
        Hashtag hashtag = hashtagService.save(dto);

        // then
        assertThat(dto.getName()).isEqualTo(hashtag.getName());
        then(hashtagRepository).should().save(any());
    }

    @Test
    @DisplayName("기존에 존재하지 않는 해쉬태그를 매장에 등록")
    void should_SaveHashtag_When_ExistNotByNameLinkedStore() {
        // given
        Store store = Store.builder()
                .name("매장")
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        Long mockStoreId = 1L;

        setField(store, "id", mockStoreId);

        HashtagDTO dto = HashtagDTO.builder()
                .name("해쉬태그")
                .build();

        Long mockHashtagId = 2L;

        setField(dto, "hashtagId", mockHashtagId);

        given(storeRepository.findById(mockStoreId)).willReturn(Optional.of(store));
        given(hashtagRepository.existsByName(dto.getName())).willReturn(true);
        given(hashtagRepository.findByName(dto.getName())).willReturn(Optional.ofNullable(dto.toEntity()));

        // when
        Long hashtagId = hashtagService.simpleSaveHashtagLinkedStore(mockStoreId, dto);

        // then
        assertThat(dto.getHashtagId()).isEqualTo(hashtagId);
        then(storeRepository).should().findById(mockStoreId);
        then(hashtagRepository).should().existsByName(dto.getName());
        then(hashtagRepository).should().findByName(dto.getName());
    }

    @Test
    @DisplayName("기존에 존재하던 해쉬태그를 매장에 등록")
    void should_SaveHashtag_When_ExistByNameLinkedStore() {
        // given
        Store store = Store.builder()
                .name("매장")
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        Long mockStoreId = 1L;

        setField(store, "id", mockStoreId);

        HashtagDTO dto = HashtagDTO.builder()
                .name("해쉬태그")
                .build();

        Long mockHashtagId = 2L;

        setField(dto, "hashtagId", mockHashtagId);

        given(storeRepository.findById(mockStoreId)).willReturn(Optional.of(store));
        given(hashtagRepository.existsByName(dto.getName())).willReturn(false);
        given(hashtagRepository.save(any())).willReturn(dto.toEntity());

        // when
        Long hashtagId = hashtagService.simpleSaveHashtagLinkedStore(mockStoreId, dto);

        // then
        assertThat(dto.getHashtagId()).isEqualTo(hashtagId);
        then(storeRepository).should().findById(mockStoreId);
        then(hashtagRepository).should().existsByName(dto.getName());
        then(hashtagRepository).should().save(any());
    }

    @Test
    @DisplayName("해쉬태그 삭제")
    void should_DeleteHashtag_When_ByHashtagId() {
        // given
        HashtagDTO dto = HashtagDTO.builder()
                .name("해쉬태그")
                .build();

        Long mockHashtagId = 2L;

        setField(dto, "hashtagId", mockHashtagId);

        Hashtag entity = dto.toEntity();

        given(hashtagRepository.findById(mockHashtagId)).willReturn(Optional.of(entity));

        // when
        Long hashtagId = hashtagService.delete(mockHashtagId);

        // then
        assertThat(dto.getHashtagId()).isEqualTo(hashtagId);
        then(hashtagRepository).should().findById(mockHashtagId);
        then(hashtagRepository).should().delete(entity);
    }

    @Test
    @DisplayName("존재하지 않는 해쉬태그 hashtagId로 조회 시 예외 발생")
    void should_ThrowException_When_ExistsNotHashtagById() {
        // given
        Long mockHashtagId = 1L;
        given(hashtagRepository.findById(1L)).willThrow(new IllegalArgumentException());

        // then
        assertThatThrownBy(() -> hashtagService.findById(mockHashtagId)).isInstanceOf(IllegalArgumentException.class);
        then(hashtagRepository).should().findById(mockHashtagId);
    }

    @Test
    @DisplayName("모든 해쉬태그 조회")
    void should_FindAllHashtags() {
        // given
        HashtagDTO dto1 = HashtagDTO.builder()
                .name("해쉬태그1")
                .build();

        HashtagDTO dto2 = HashtagDTO.builder()
                .name("해쉬태그2")
                .build();

        setField(dto1, "hashtagId", 1L);
        setField(dto2, "hashtagId", 2L);

        Hashtag entity1 = dto1.toEntity();
        Hashtag entity2 = dto2.toEntity();

        List<Hashtag> hashtagList = new ArrayList<>();
        hashtagList.add(entity1);
        hashtagList.add(entity2);

        given(hashtagRepository.findAll()).willReturn(hashtagList);

        // when
        List<HashtagDTO> dtoList = hashtagService.findAll();

        // then
        assertThat(dtoList).hasSize(2).contains(dto1, dto2);
        then(hashtagRepository).should().findAll();
    }

    @Test
    @DisplayName("이름으로 해쉬태그 조회")
    void should_FindHashtag_When_ByName() {
        // given
        HashtagDTO dto = HashtagDTO.builder()
                .name("해쉬태그1")
                .build();

        setField(dto, "hashtagId", 1L);

        Hashtag entity = dto.toEntity();

        given(hashtagRepository.findByName(dto.getName())).willReturn(Optional.ofNullable(entity));

        // when
        Hashtag hashtag = hashtagService.findByName(dto.getName());

        // then
        assertThat(dto.getName()).isEqualTo(hashtag.getName());
        then(hashtagRepository).should().findByName(dto.getName());
    }

    @Test
    @DisplayName("존재하지 않는 해쉬태그 이름으로 조회 시 예외 발생")
    void should_ThrowException_When_ExistsNotHashtagByName() {
        // given
        String name = "해쉬태그 이름";
        given(hashtagRepository.findByName(name)).willThrow(new IllegalArgumentException());

        // then
        assertThatThrownBy(() -> hashtagService.findByName(name)).isInstanceOf(IllegalArgumentException.class);
        then(hashtagRepository).should().findByName(name);
    }
}
