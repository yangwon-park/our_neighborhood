package ywphsm.ourneighbor.repository.menu;

import ywphsm.ourneighbor.domain.menu.Menu;

import java.util.List;

public interface MenuRepositoryCustom {
    List<Menu> findByStoreIdCaseByOrderByType(Long storeId);
}
