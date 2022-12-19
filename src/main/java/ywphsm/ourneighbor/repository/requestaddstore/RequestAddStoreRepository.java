package ywphsm.ourneighbor.repository.requestaddstore;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.store.RequestAddStore;

public interface RequestAddStoreRepository extends JpaRepository<RequestAddStore, Long>, RequestAddStoreRepositoryCustom {
}
