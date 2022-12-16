package ywphsm.ourneighbor.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ywphsm.ourneighbor.repository.store.StoreRepository;
import ywphsm.ourneighbor.service.store.StoreService;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    @BeforeEach
    void beforeEach() {

    }

    @Test
    @DisplayName("매장 저장")
    void should_SaveAStore() {

    }
}
