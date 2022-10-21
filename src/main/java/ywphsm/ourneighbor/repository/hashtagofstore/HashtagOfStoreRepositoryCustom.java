package ywphsm.ourneighbor.repository.hashtagofstore;

import ywphsm.ourneighbor.domain.dto.HashtagOfStoreDTO;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfStore;

import java.util.List;

public interface HashtagOfStoreRepositoryCustom {

    List<HashtagOfStoreDTO.WithCount> findHashtagAndCountByStoreIdTop9(Long storeId);

    List<HashtagOfStoreDTO.WithCount> findAllHashtagAndCountByStoreId(Long storeId);

    List<HashtagOfStore> findAllHashtagByStoreId(Long storeId);

    Long deleteByHashtagIdByStoreId(Long hashtagId, Long storeId);

}
