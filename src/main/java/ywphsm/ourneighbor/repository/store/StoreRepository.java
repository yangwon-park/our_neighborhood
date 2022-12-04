package ywphsm.ourneighbor.repository.store;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.store.Store;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {

}