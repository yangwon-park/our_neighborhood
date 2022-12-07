package ywphsm.ourneighbor.repository.hashtag.hashtagofmenu;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu;

import java.util.List;

import static ywphsm.ourneighbor.domain.hashtag.QHashtagOfMenu.*;

@Slf4j
@RequiredArgsConstructor
public class HashtagOfMenuRepositoryImpl implements HashtagOfMenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HashtagOfMenu> findAllHashtagByMenuId(Long menuId) {
        return queryFactory
                .select(hashtagOfMenu)
                .from(hashtagOfMenu)
                .where(hashtagOfMenu.menu.id.eq(menuId))
                .fetch();
    }
}
