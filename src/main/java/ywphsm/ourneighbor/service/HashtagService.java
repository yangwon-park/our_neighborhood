package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.hashtag.HashtagRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ywphsm.ourneighbor.domain.hashtag.HashtagOfStore.linkHashtagAndStore;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    private final StoreRepository storeRepository;

    @Transactional
    public Hashtag save(HashtagDTO dto) {
        return hashtagRepository.save(dto.toEntity());
    }

    @Transactional
    public Long simpleSaveHashtagLinkedStore(Long storeId, HashtagDTO dto) {
        Store findStore = storeRepository.findById(storeId).orElseThrow(
                () -> new IllegalArgumentException("해당 매장이 없습니다. storeId = " + storeId));

        boolean duplicateCheck = hashtagRepository.existsByName(dto.getName());

        Hashtag newHashtag;

        if (!duplicateCheck) {
            newHashtag = save(dto);
        } else {
            newHashtag = hashtagRepository.findByName(dto.getName())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 해쉬태그입니다."));
        }

        linkHashtagAndStore(newHashtag, findStore);

        return newHashtag.getId();
    }

    @Transactional
    public Long delete(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.findById(hashtagId).orElseThrow(
                () -> new IllegalArgumentException("해당 해쉬태그가 없습니다. hashtagId = " + hashtagId));

        hashtagRepository.delete(hashtag);

        return hashtagId;
    }

    public HashtagDTO findById(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.findById(hashtagId).orElseThrow(
                () -> new IllegalArgumentException("해당 해쉬태그가 없습니다. hashtagId = " + hashtagId));

        return HashtagDTO.of(hashtag);
    }

    public List<HashtagDTO> findAll() {
        return hashtagRepository.findAll().stream()
                .map(HashtagDTO::new).collect(Collectors.toList());
    }

    public Hashtag findByName(String name) {
        return hashtagRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 해쉬태그입니다."));
    }
}