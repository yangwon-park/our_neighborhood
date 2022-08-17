package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ywphsm.ourneighbor.domain.Address;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;
import ywphsm.ourneighbor.domain.store.days.Days;
import ywphsm.ourneighbor.domain.store.days.DaysEntity;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit() {

            Member member1 = new Member("ailey", "12341234", "박양원", "양갱",
                    "ywonp9405@gmail.com", "01067731582", 29, 0);

            Member member2 = new Member("hansung", "12341234", "문한성", "한성",
                    "hansung@naver.com", "01038352375", 24, 0);

            List<String> offDays = new ArrayList<>();
            offDays.add("일요일");
            offDays.add("토요일");

            Store store1 = new Store("칸다 소바", 35.1612928, 129.1600985, "0517311660",
                    LocalTime.of(9, 00), LocalTime.of(21, 00), LocalTime.of(15, 30), LocalTime.of(17, 00),
                    null, "안녕하세요 칸다 소바입니다.", null , StoreStatus.OPEN, new Address("부산 해운대구 구남로 30번길 8-3", "", "48094", "1층"));
            Store store2 = new Store("맥도날드", 35.1600985, 129.1596415, "07072091629",
                    LocalTime.of(00, 00), LocalTime.of(00, 00), null, null,
                    null, "맥도날드로 오세요.", offDays, StoreStatus.OPEN, new Address("부산 해운대구 해운대로 570번길 51", "", "48094", ""));
            Store store3 = new Store("쿠지라멘", 35.1638127, 129.1681053, null,
                    LocalTime.of(11, 00), LocalTime.of(19, 00), null, null,
                    "영업 날짜 및 시간을 인스타 그램으로 확인해주세요.", "https://www.instagram.com/kuziramen/", null, StoreStatus.OPEN, new Address("부산 해운대구 해운대해변로 35101", "", "48096", null));
            Store store4 = new Store("버거인뉴욕", 35.1609342, 129.1646901, "0517430144",
                    LocalTime.of(11, 00), LocalTime.of(21, 00), null, null,
                    null, null, null, StoreStatus.OPEN, new Address("부산 해운대구 해운대해변로 298번길 9", "", "48099", null));
            Store store5 = new Store("퍼플펍", 35.1641708, 129.1654434, null,
                    LocalTime.of(17, 00), LocalTime.of(02, 00), null, null,
                    null, null, null, StoreStatus.OPEN, new Address("부산 해운대구 중1동 중동2로 24번길", "", "48096", null));
            Store store6 = new Store("맛찬들 왕소금구이 해운대점", 35.1610265, 129.1589194, "0517420106",
                    LocalTime.of(16, 00), LocalTime.of(01, 00), null, null,
                    null, null, null, StoreStatus.OPEN, new Address("부산 해운대구 구남로 24번길 29", "", "48094", null));
            Store store7 = new Store("아웃닭", 35.1602022, 129.1620121, "0517479788",
                    LocalTime.of(17, 30), LocalTime.of(02, 00), null, null,
                    null, null, null, StoreStatus.OPEN, new Address("부산 해운대구 구남로 47", "", "", null));
            Store store8 = new Store("류센소", 35.1615938, 129.1562055, "01082813884",
                    LocalTime.of(9, 00), LocalTime.of(00, 00), null, null,
                    null, null, null, StoreStatus.OPEN, new Address("부산 해운대구 구남로8번길 62", "", "48094", null));
            Store store9 = new Store("버거샵", 35.1642173, 129.1576517, "0517474961",
                    LocalTime.of(11, 30), LocalTime.of(20, 30), null, null,
                    null, null, null, StoreStatus.OPEN, new Address("부산 해운대구 우동1로20번길 19", "", "", ""));
            Store store10 = new Store("나가하마 만게츠 한국 본점", 35.1666969, 129.1574575, "0517310866",
                    LocalTime.of(11, 00), LocalTime.of(20, 30), LocalTime.of(15, 30), LocalTime.of(16, 30),
                    null, null, null, StoreStatus.OPEN, new Address("부산 해운대구 우동1로 57", "", "48088", null));
            Store store11 = new Store("코지하우스", 35.1646947, 129.1569286, "0517316683",
                    LocalTime.of(11, 30), LocalTime.of(22, 30), null, null,
                    null, null, null, StoreStatus.OPEN, new Address("부산 해운대구 우동1로 37", "", "48088", null));

            em.persist(member1);
            em.persist(member2);
            em.persist(store1);
            em.persist(store2);
            em.persist(store3);
            em.persist(store4);
            em.persist(store5);
            em.persist(store6);
            em.persist(store7);
            em.persist(store8);
            em.persist(store9);
            em.persist(store10);
            em.persist(store11);
        }
    }
}