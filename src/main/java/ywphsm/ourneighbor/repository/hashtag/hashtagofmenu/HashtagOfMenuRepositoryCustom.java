package ywphsm.ourneighbor.repository.hashtag.hashtagofmenu;

import ywphsm.ourneighbor.domain.hashtag.HashtagOfMenu;

import java.util.List;

public interface HashtagOfMenuRepositoryCustom {

    List<HashtagOfMenu> findAllHashtagByMenuId(Long menuId);

    Long deleteByHashtagIdByMenuId(Long menuId);
}
