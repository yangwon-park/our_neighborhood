package ywphsm.ourneighbor.repository.store.days;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.store.days.Days;

import java.util.List;

public interface DaysRepository extends JpaRepository<Days, Long>, DaysRepositoryCustom {

    List<Days> findAllByOrderByIdAsc();

}