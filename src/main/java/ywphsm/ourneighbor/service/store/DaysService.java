package ywphsm.ourneighbor.service.store;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.dto.store.days.DaysDTO;
import ywphsm.ourneighbor.domain.dto.store.days.DaysOfStoreDTO;
import ywphsm.ourneighbor.domain.store.days.Days;
import ywphsm.ourneighbor.domain.store.days.DaysOfStore;
import ywphsm.ourneighbor.repository.store.days.DaysRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DaysService {

    private final DaysRepository daysRepository;

    public List<DaysDTO> findAllByOrderByIdAsc() {
        return daysRepository.findAllByOrderByIdAsc().stream()
                .map(Days::of).collect(Collectors.toList());
    }

    public Days findById(Long daysId) {
        return daysRepository.findById(daysId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요일입니다."));
    }

    public List<Long> getDaysByStoreId(Long storeId) {
        List<DaysOfStore> dos = daysRepository.getDaysByStoreId(storeId);
        return dos.stream().map(daysOfStore -> daysOfStore.getDays().getId()).collect(Collectors.toList());
    }
}
