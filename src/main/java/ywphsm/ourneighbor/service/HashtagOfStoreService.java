package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagOfStoreDTO;
import ywphsm.ourneighbor.repository.hashtag.hashtagofstore.HashtagOfStoreRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HashtagOfStoreService {

    private final HashtagOfStoreRepository hashtagOfStoreRepository;

    public List<HashtagOfStoreDTO.WithCount> findHashtagAndCountByStoreIdTop9(Long storeId) {
        return hashtagOfStoreRepository.findHashtagAndCountByStoreIdTop9(storeId);
    }

    public List<HashtagOfStoreDTO.WithCount> findAllHashtagAndCountByStoreId(Long storeId) {
        return hashtagOfStoreRepository.findAllHashtagAndCountByStoreId(storeId);
    }

    public Long deleteHashtagOfStore(Long hashtagId, Long storeId) {
        return hashtagOfStoreRepository.deleteByHashtagIdByStoreId(hashtagId, storeId);
    }


}
