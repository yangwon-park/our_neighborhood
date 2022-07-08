package ywphsm.ourneighbor.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Address;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.QReview;
import ywphsm.ourneighbor.domain.Review;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.QMember;
import ywphsm.ourneighbor.domain.store.QStore;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static ywphsm.ourneighbor.domain.QReview.*;
import static ywphsm.ourneighbor.domain.member.QMember.*;
import static ywphsm.ourneighbor.domain.store.QStore.*;

@SpringBootTest
@Transactional
@Rollback(false)
class ReviewServiceTest {

    @Autowired
    ReviewService reviewService;

    @Autowired
    MemberService memberService;

    @Autowired
    MenuService menuService;

    @Autowired
    StoreService storeService;

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);

        Member member1 = new Member("kkk", "kkk", "user1",
                "유저1", null, "010-1234-1234", 19, 0);

        Member member2 = new Member("JJJ", "JJJ", "user2",
                "유저2", null, "010-1234-1234", 35, 1);

        Member member3 = new Member("ㅁㅁㅁ", "ㅁㅁㅁ", "user3",
                "유저3", null, "010-1234-1234", 25, 0);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        Store store1 = new Store(
                "칸다 소바", 123L, 123L, "010-1234-1234",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                "안녕하세요", "칸다 소바입니다.", null, StoreStatus.OPEN,
                new Address("부산광역시", "해운대구", "123489")
        );
        Store store2 = new Store(
                "맥도 날드", 123L, 123L, "010-1234-1234",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                "안녕하세요", "맥도날드입니다.", null, StoreStatus.OPEN,
                new Address("부산광역시", "해운대구", "123489")
        );

        em.persist(store1);
        em.persist(store2);

        Menu menu1 = new Menu(
                "마제 소바", 10000, 0,
                null, null, store1
        );
        Menu menu2 = new Menu(
                "아우라 소바", 12000, 0,
                null, null, store1
        );
        Menu menu3 = new Menu(
                "메밀 소바", 8000, 0,
                null, null, store1
        );
        Menu menu4 = new Menu(
                "햄버거", 7000, 0,
                null, null, store2
        );
        Menu menu5 = new Menu(
                "감자 튀김", 2000, 0,
                null, null, store2
        );

        em.persist(menu1);
        em.persist(menu2);
        em.persist(menu3);
        em.persist(menu4);
        em.persist(menu5);


        Review review1 = new Review("맛있어요1", 5, member1, store1);
        Review review2 = new Review("맛있어요2", 4, member1, store1);
        Review review3 = new Review("맛있어요3", 3, member2, store2);
        Review review4 = new Review("맛있어요4", 2, member2, store2);
        Review review5 = new Review("맛있어요5", 1, member2, store2);

        em.persist(review1);
        em.persist(review2);
        em.persist(review3);
        em.persist(review4);
        em.persist(review5);
    }

    @Test
    @DisplayName("리뷰 등록")
    void saveReview() {
        Member findMember = findMember("JJJ");

        Store visitedStore = findStore("칸다 소바");

        Review review = new Review("맛있어요", 5, findMember, visitedStore);

        Long reviewId = reviewService.saveReview(review);

        Long findReviewId = reviewService.findOne(reviewId).getId();

        assertThat(reviewId).isEqualTo(findReviewId);
    }

    @Test
    @DisplayName("Querydsl 사용해서 리뷰의 내용, 작성자, 가게 이름 불러오기")
    void findReviewWhoWhichWithQuerydsl() {
        List<Tuple> result = queryFactory
                .select(review.content, review.member.username, review.store.name)
                .from(review)
                .fetch();

        assertThat(result.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("JJJ가 작성한 리뷰 불러오기")
    void findReviewsByWrittenWho() {
        List<Review> reviewList = queryFactory
                .select(review)
                .from(review)
                .where(review.member.username.eq("JJJ"))
                .fetch();

        assertThat(reviewList).extracting("content")
                .contains("맛있어요3", "맛있어요4", "맛있어요5");
    }

    @Test
    @DisplayName("첫번째 리뷰가 적힌 가게의 메뉴 불러오기")
    void findMenuWithReview() {
        Review firstReview = reviewService.findAllReviews().get(0);

        Store store = firstReview.getStore();

        System.out.println("store = " + store.getName());

        List<Menu> menuList = store.getMenuList();
        System.out.println("menuList = " + menuList);

        for (Menu menu : menuList) {
            System.out.println("menu = " + menu);
        }
    }



    private Store findStore(String name) {
        return queryFactory
                .select(store)
                .from(store)
                .where(store.name.eq(name))
                .fetchOne();
    }

    private Member findMember(String name) {
        return queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq(name))
                .fetchOne();
    }

}