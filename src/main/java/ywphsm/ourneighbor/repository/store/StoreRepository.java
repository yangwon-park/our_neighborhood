package ywphsm.ourneighbor.repository.store;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.store.Store;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {

    List<Store> findByName(String name);


}
