package ywphsm.ourneighbor.repository.hashtagofstore;

import ywphsm.ourneighbor.domain.hashtag.HashtagOfStore;
import ywphsm.ourneighbor.repository.hashtagofstore.dto.HashtagOfStoreCountDTO;

import java.util.List;

public interface HashtagOfStoreRepositoryCustom {

    List<HashtagOfStoreCountDTO> findHashtagCountGroupByStoreTop9();
}
