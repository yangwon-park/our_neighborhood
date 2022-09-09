package ywphsm.ourneighbor.repository.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuRepositoryCustom {
    Menu findByName(String name);
}
