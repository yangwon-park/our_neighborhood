package ywphsm.ourneighbor.repository.store;

import org.locationtech.jts.io.ParseException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;

import java.util.List;

public interface StoreRepositoryCustom {

    List<Store> searchByKeyword(String keyword);

    List<Store> searchByCategory(Long categoryId);

    Slice<SimpleSearchStoreDTO> searchByHashtag(List<Long> hashtagIdList, double nex, double ney,
                                                double nwx, double nwy, double swx, double swy,
                                                double sex, double sey, Pageable pageable) throws ParseException;
}