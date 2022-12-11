package ywphsm.ourneighbor.repository.hashtag.hashtagofstore;

import ywphsm.ourneighbor.domain.dto.hashtag.HashtagOfStoreDTO;

import java.util.List;

public interface HashtagOfStoreRepositoryCustom {

    List<HashtagOfStoreDTO.WithCount> findHashtagAndCountByStoreIdOrderByCountDescTop9(Long storeId);

    List<HashtagOfStoreDTO.WithCount> findHashtagAndCountByStoreIdOrderByCountDescOrderByHashtagName(Long storeId);

    Long deleteByHashtagIdByStoreId(Long hashtagId, Long storeId);
}
