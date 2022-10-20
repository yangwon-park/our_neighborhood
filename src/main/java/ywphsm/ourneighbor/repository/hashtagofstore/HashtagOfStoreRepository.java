package ywphsm.ourneighbor.repository.hashtagofstore;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfStore;

public interface HashtagOfStoreRepository extends JpaRepository<HashtagOfStore, Long>, HashtagOfStoreRepositoryCustom {
}
