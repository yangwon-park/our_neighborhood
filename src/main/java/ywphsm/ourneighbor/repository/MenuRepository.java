package ywphsm.ourneighbor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.Menu;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    Menu findByName(String name);
}
