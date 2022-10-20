package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.repository.hashtagofstore.HashtagOfStoreRepository;
import ywphsm.ourneighbor.repository.hashtagofstore.dto.HashtagOfStoreCountDTO;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HashtagOfStoreService {

    private final HashtagOfStoreRepository hashtagOfStoreRepository;

    public List<HashtagOfStoreCountDTO> findHashtagCountGroupByStoreTop9() {
        return hashtagOfStoreRepository.findHashtagCountGroupByStoreTop9();
    }
}
