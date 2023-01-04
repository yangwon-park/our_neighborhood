package ywphsm.ourneighbor.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.member.MemberOfStore;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.member.MemberOfStoreRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PessimisticLockStockTest {

    @Autowired
    ReviewService reviewService;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    MemberOfStoreRepository memberOfStoreRepository;

    @Test
    public void updateRating_test() {
        ReviewDTO.Add dto = ReviewDTO.Add.builder()
                .memberId(37001L)
                .storeId(40016L)
                .rating(1)
                .content("123")
                .build();

        reviewService.updateStoreRating(dto.getStoreId(), dto.toEntity(), true);

        Store store = storeRepository.findById(40016L).orElseThrow();
        // 100 - 1 = 99

        assertEquals(1, store.getRatingTotal());
    }

    @Test
    public void 동시에_100명() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        ReviewDTO.Add dto = ReviewDTO.Add.builder()
                .memberId(37001L)
                .storeId(40016L)
                .rating(1)
                .content("123")
                .build();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    reviewService.updateStoreRating(dto.getStoreId(), dto.toEntity(), true);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Store store = storeRepository.findById(40016L).orElseThrow();

        // 100 - (100 * 1) = 0
        assertEquals(2, store.getRatingTotal());
    }

}
