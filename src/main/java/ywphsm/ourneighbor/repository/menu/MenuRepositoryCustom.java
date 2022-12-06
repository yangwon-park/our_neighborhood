package ywphsm.ourneighbor.repository.menu;

import ywphsm.ourneighbor.domain.menu.Menu;

import java.util.List;

public interface MenuRepositoryCustom {

    List<Menu> findByStoreIdWithoutMenuTypeIsMenuCaseByOrderByType(Long storeId);

    List<String> findImageByMenuTypeIsMenu(Long storeId);
}
