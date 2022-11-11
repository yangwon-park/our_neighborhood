package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.RecommendPost;
import ywphsm.ourneighbor.domain.dto.RecommendPostDTO;
import ywphsm.ourneighbor.repository.recommendpost.RecommendPostRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RecommendPostService {

    private final RecommendPostRepository recommendPostRepository;

    @Transactional
    public Long save(RecommendPostDTO.Add dto) {
        RecommendPost recommendPost = dto.toEntity();

        return recommendPostRepository.save(recommendPost).getId();
    }
}
