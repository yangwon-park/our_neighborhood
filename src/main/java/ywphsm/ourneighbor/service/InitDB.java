package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init() {
//        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit() {

            Store store1 = new Store("칸다소바", 35.1612928, 129.1600985, "0517311660",
                    new BusinessTime(LocalTime.of(11, 30), LocalTime.of(21, 30), LocalTime.of(15, 30), LocalTime.of(17, 00)),
                    null, "안녕하세요 칸다 소바입니다.", null , StoreStatus.OPEN,
                    new Address("부산 해운대구 구남로30번길 8-3", "부산 해운대구 우동 544-15", "48094", "1층"));

            Store store3 = new Store("쿠지라멘", 35.1638127, 129.1681053, null,
                    new BusinessTime(LocalTime.of(11, 00), LocalTime.of(19, 00), null, null),
                    "영업 날짜 및 시간을 인스타그램으로 확인해주세요.", "https://www.instagram.com/kuziramen/", null, StoreStatus.OPEN,
//                    월요일 휴무
                    new Address("부산 해운대구 해운대해변로 351-1", "부산 해운대구 중동 1205-13", "48096", ""));

            Store store4 = new Store("버거인뉴욕", 35.1609342, 129.1646901, "0517430144",
                    new BusinessTime(LocalTime.of(11, 00), LocalTime.of(21, 00), null, null),
                    null, null, null, StoreStatus.OPEN,
                    new Address("부산 해운대구 해운대해변로298번길 9", "부산 해운대구 중동 1404-39", "48099", ""));

            Store store5 = new Store("퍼플펍", 35.1641708, 129.1654434, null,
                    new BusinessTime(LocalTime.of(17, 00), LocalTime.of(02, 00), null, null),
                    null, null, null, StoreStatus.OPEN,
                    new Address("부산광역시 해운대구 달맞이길 2-7", "부산 해운대구 중동 1128-86", "48099", ""));

            Store store8 = new Store("류센소", 35.1615938, 129.1562055, "01082813884",
                    new BusinessTime(LocalTime.of(9, 00), LocalTime.of(00, 00), null, null),
                    null, null, null, StoreStatus.OPEN,
                    new Address("부산 해운대구 구남로8번길 62", "부산 해운대구 우동 641-15", "48094", ""));

            Store store9 = new Store("버거샵", 35.1642173, 129.1576517, "0517474961",
                    new BusinessTime(LocalTime.of(11, 30), LocalTime.of(20, 30), null, null),
                    null, null, null, StoreStatus.OPEN,
                    new Address("부산 해운대구 우동1로20번길 19", "부산 해운대구 우동 532-17", "48087", ""));

            Store store10 = new Store("나가하마 만게츠 한국 본점", 35.1666969, 129.1574575, "0517310866",
                    new BusinessTime(LocalTime.of(11, 00), LocalTime.of(20, 30), LocalTime.of(15, 30), LocalTime.of(16, 30)),
                    null, null, null, StoreStatus.OPEN,
                    new Address("부산 해운대구 우동1로 57", "부산 해운대구 우동 397-22", "48088", ""));

            Store store11 = new Store("코지하우스", 35.1646947, 129.1569286, "0517316683",
                    new BusinessTime(LocalTime.of(11, 30), LocalTime.of(22, 30), null, null),
                    null, null, null, StoreStatus.OPEN,
                    new Address("부산 해운대구 우동1로 37", "부산 해운대구 우동 493-1", "48088", ""));

            em.persist(store3);
            em.persist(store4);
            em.persist(store5);
            em.persist(store8);
            em.persist(store9);
            em.persist(store10);
            em.persist(store11);
        }
    }
}