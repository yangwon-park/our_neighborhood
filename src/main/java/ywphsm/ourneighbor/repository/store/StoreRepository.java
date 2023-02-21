package ywphsm.ourneighbor.repository.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {

    List<Store> findByName(String name);

    /**
     * OptimisticLock 사용하기 위함
     * 동시성 문제 해결, 사용중일때 락을걸어서 접근을 막아줌(동일한 값의로의 수정 접근)
     */
    @Lock(value = LockModeType.OPTIMISTIC)
    Optional<Store> findWithOptimisticLockById(Long id);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Store> findWithPessimisticLockById(Long id);
}