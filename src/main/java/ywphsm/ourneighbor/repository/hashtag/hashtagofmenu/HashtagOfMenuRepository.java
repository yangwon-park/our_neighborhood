package ywphsm.ourneighbor.repository.hashtag.hashtagofmenu;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu;
import ywphsm.ourneighbor.domain.menu.Menu;

import java.util.List;

public interface HashtagOfMenuRepository extends JpaRepository<HashtagOfMenu, Long>, HashtagOfMenuRepositoryCustom {

    HashtagOfMenu findByHashtag(Hashtag hashtag);

    List<HashtagOfMenu> findByMenu(Menu menu);

    void deleteByMenu(Menu menu);

    void deleteByHashtag(Hashtag hashtag);

    Boolean existsByHashtagAndMenu(Hashtag hashtag, Menu menu);

}
