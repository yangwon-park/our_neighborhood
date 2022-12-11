package ywphsm.ourneighbor.repository.hashtag.hashtagofmenu;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu;
import ywphsm.ourneighbor.domain.menu.Menu;

public interface HashtagOfMenuRepository extends JpaRepository<HashtagOfMenu, Long>, HashtagOfMenuRepositoryCustom {

    HashtagOfMenu findByHashtagAndMenu(Hashtag hashtag, Menu menu);

    void deleteByMenu(Menu menu);

    void deleteByHashtag(Hashtag hashtag);

    void deleteByHashtagAndMenu(Hashtag hashtag, Menu menu);

    Boolean existsByHashtagAndMenu(Hashtag hashtag, Menu menu);
}
