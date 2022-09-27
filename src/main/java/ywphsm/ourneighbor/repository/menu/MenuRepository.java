package ywphsm.ourneighbor.repository.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.store.Store;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuRepositoryCustom {
    Menu findByName(String name);

    Boolean existsByNameAndStore(String name, Store store);
}
