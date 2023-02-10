package ywphsm.ourneighbor.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.domain.store.Review;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.review.ReviewRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewRepositoryTest {

    @Autowired
    TestEntityManager tem;

    @Autowired
    ReviewRepository reviewRepository;

    @BeforeEach
    void beforeEach() {

        Member member1 = Member.builder()
                .userId("member1")
                .password("1234")
                .username("이름1")
                .nickname("닉네임1")
                .email("email1@naver.com")
                .phoneNumber("01011111111")
                .birthDate("20000101")
                .age(24)
                .gender(1)
                .role(Role.USER)
                .build();


        UploadFile uploadFile1 = new UploadFile("업로드명1", "저장명1", "URL1");

        tem.persist(member1);

        uploadFile1.addMember(member1);

        Store store1 = Store.builder()
                .name("매장1")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .lat(35.1710366410643)
                .lon(129.175759994618)
                .point(point(WGS84, g(129.175759994618, 35.1710366410643)))
                .businessTime(new BusinessTime(LocalTime.now(), LocalTime.now(), null, null, null))
                .categoryOfStoreList(new ArrayList<>())
                .hashtagOfStoreList(new ArrayList<>())
                .build();

        UploadFile uploadFile2 = new UploadFile("업로드명2", "저장명2", "URL2");

        tem.persist(store1);

        uploadFile2.addStore(store1);

        Review review1 = Review.builder()
                .content("리뷰내용1")
                .rating(5)
                .build();

        Review review2 = Review.builder()
                .content("리뷰내용2")
                .rating(5)
                .build();

        Review review3 = Review.builder()
                .content("리뷰내용3")
                .rating(5)
                .build();

        Review review4 = Review.builder()
                .content("리뷰내용4")
                .rating(5)
                .build();

        Review review5 = Review.builder()
                .content("리뷰내용5")
                .rating(5)
                .build();

        Review review6 = Review.builder()
                .content("리뷰내용6")
                .rating(5)
                .build();

        review1.setStore(store1);
        review1.setMember(member1);

        review2.setStore(store1);
        review2.setMember(member1);

        review3.setStore(store1);
        review3.setMember(member1);

        review4.setStore(store1);
        review4.setMember(member1);

        review5.setStore(store1);
        review5.setMember(member1);

        review6.setStore(store1);
        review6.setMember(member1);

        tem.persist(review1);
        tem.persist(review2);
        tem.persist(review3);
        tem.persist(review4);
        tem.persist(review5);
        tem.persist(review6);

        UploadFile uploadFile3 = new UploadFile("업로드명3", "저장명3", "URL3");
        UploadFile uploadFile4 = new UploadFile("업로드명4", "저장명4", "URL4");
        UploadFile uploadFile5 = new UploadFile("업로드명5", "저장명5", "URL5");
        UploadFile uploadFile6 = new UploadFile("업로드명6", "저장명6", "URL6");
        UploadFile uploadFile7 = new UploadFile("업로드명7", "저장명7", "URL7");
        UploadFile uploadFile8 = new UploadFile("업로드명8", "저장명8", "URL8");
        UploadFile uploadFile9 = new UploadFile("업로드명9", "저장명9", "URL9");
        UploadFile uploadFile10 = new UploadFile("업로드명10", "저장명10", "URL10");

        List<UploadFile> fileList1 = new ArrayList<>();
        fileList1.add(uploadFile3);
        fileList1.add(uploadFile4);
        review1.addFile(fileList1);

        List<UploadFile> fileList2 = new ArrayList<>();
        fileList2.add(uploadFile5);
        review2.addFile(fileList2);

        List<UploadFile> fileList3 = new ArrayList<>();
        fileList3.add(uploadFile6);
        review3.addFile(fileList3);

        List<UploadFile> fileList4 = new ArrayList<>();
        fileList4.add(uploadFile7);
        review4.addFile(fileList4);

        List<UploadFile> fileList5 = new ArrayList<>();
        fileList5.add(uploadFile8);
        review5.addFile(fileList5);

        List<UploadFile> fileList6 = new ArrayList<>();
        fileList6.add(uploadFile9);
        fileList6.add(uploadFile10);
        review6.addFile(fileList6);

    }

    @Test
    @DisplayName("리뷰 저장")
    void should_SaveAReview() {
        Review review = Review.builder()
                .content("리뷰내용6")
                .rating(5)
                .build();

        Review savedReivew = reviewRepository.save(review);

        assertThat(savedReivew.getContent()).isEqualTo(review.getContent());
    }

    @Test
    @DisplayName("리뷰 삭제시 IsEmpty 확인")
    void should_IsEmpty_When_DeleteAReview() {
        Review review = Review.builder()
                .content("리뷰내용6")
                .rating(5)
                .build();

        tem.persist(review);

        reviewRepository.deleteById(review.getId());

        Optional<Review> findReview = reviewRepository.findById(review.getId());

        assertThat(findReview).isEmpty();
    }

    @Test
    @DisplayName("리뷰 이미지 가져오기")
    void should_findFile_When_ReviewId() {
        List<Review> reviewList = reviewRepository.findByContent("리뷰내용1");

        Review review = reviewList.get(0);

        List<String> imageUrls = reviewRepository.reviewImageUrl(review.getId());

        assertThat(imageUrls.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("회원 리뷰 전체 조회")
    void should_findReview_When_MemberId() {
        List<Review> reviewList = reviewRepository.findByContent("리뷰내용1");

        Review review = reviewList.get(0);

        List<ReviewMemberDTO> reviewMemberDTOS = reviewRepository.myReview(review.getMember().getId());

        assertThat(reviewMemberDTOS.size()).isEqualTo(6);
    }

    @Test
    @DisplayName("가게 리뷰 페이징으로 조회")
    void should_findReview_When_StoreId_And_Pageable() {
        List<Review> reviewList = reviewRepository.findByContent("리뷰내용1");

        Review review = reviewList.get(0);

        PageRequest pageRequest = PageRequest.of(0, 5);
        Slice<ReviewMemberDTO> reviewMemberDTOS = reviewRepository.reviewPage(pageRequest, review.getStore().getId());

        assertThat(reviewMemberDTOS.hasNext()).isTrue();
        assertThat(reviewMemberDTOS.getSize()).isEqualTo(5);
        assertThat(reviewMemberDTOS.getNumber()).isEqualTo(0);

        pageRequest = PageRequest.of(1, 5);
        reviewMemberDTOS = reviewRepository.reviewPage(pageRequest, review.getStore().getId());

        assertThat(reviewMemberDTOS.hasNext()).isFalse();
        assertThat(reviewMemberDTOS.getNumber()).isEqualTo(1);
    }
}
