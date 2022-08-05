package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ywphsm.ourneighbor.domain.Address;
import ywphsm.ourneighbor.domain.member.Member;
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

            Store store1 = new Store("칸다 소바", 35.1612928, 129.1600985, "0517311660",
                    LocalTime.of(9, 00, 00), LocalTime.of(20, 00, 00), LocalTime.of(15, 30, 00), LocalTime.of(17, 00, 00),
                    null, "안녕하세요 칸다 소바입니다.", null, StoreStatus.OPEN, new Address("부산 광역시", "해운대구 구남로 30번길 8-3 1층", "48094"));

            Store store2 = new Store("맥도날드", 35.1600985, 129.1596415, "07072091629",
                    LocalTime.of(00, 00, 00), LocalTime.of(00, 00, 00), null, null,
                    null, "맥도날드로 오세요.", null, StoreStatus.OPEN, new Address("부산 광역시", "해운대구 해운대로 570번길 51", "48094"));

            em.persist(member1);
            em.persist(member2);
            em.persist(store1);
            em.persist(store2);
        }
    }


}
