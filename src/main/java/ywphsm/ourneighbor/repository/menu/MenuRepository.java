package ywphsm.ourneighbor.repository.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.menu.Menu;
<<<<<<< HEAD
=======
import ywphsm.ourneighbor.domain.store.Store;
>>>>>>> main

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuRepositoryCustom {
    Menu findByName(String name);

    Boolean existsByNameAndStore(String name, Store store);
}
