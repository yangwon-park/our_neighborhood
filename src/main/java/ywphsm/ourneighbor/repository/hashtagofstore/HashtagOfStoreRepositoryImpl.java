package ywphsm.ourneighbor.repository.hashtagofstore;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfStore;
import ywphsm.ourneighbor.repository.hashtagofstore.dto.HashtagOfStoreCountDTO;

import java.util.List;

import static ywphsm.ourneighbor.domain.hashtag.QHashtagOfStore.*;

@Slf4j
@RequiredArgsConstructor
public class HashtagOfStoreRepositoryImpl implements HashtagOfStoreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HashtagOfStoreCountDTO> findHashtagCountGroupByStoreTop9() {
        return queryFactory
                .select(
                        Projections.constructor(HashtagOfStoreCountDTO.class,
                                hashtagOfStore.hashtag.id, hashtagOfStore.store.id,
                                hashtagOfStore.id.count().as("count"))
                )
                .from(hashtagOfStore)
                .groupBy(hashtagOfStore.hashtag, hashtagOfStore.store)
                .orderBy(hashtagOfStore.id.count().desc())
                .limit(9)
                .fetch();
    }
}
