package ywphsm.ourneighbor.unit.service;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.domain.dto.hashtag.HashtagDTO;
import ywphsm.ourneighbor.domain.file.AwsS3FileStore;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.hashtag.Hashtag;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.store.Review;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.repository.review.ReviewRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;
import ywphsm.ourneighbor.service.ReviewService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    StoreRepository storeRepository;

    @InjectMocks
    ReviewService reviewService;

    @Spy
    @InjectMocks
    AwsS3FileStore awsS3FileStore;


    @Test
    @DisplayName("리뷰 저장")
    void should_SaveReview() throws IOException, ParseException {
        // given
        Store store = Store.builder()
                .name("매장")
                .build();

        Long mockStoreId = 1L;

        setField(store, "id", mockStoreId);

        Member member = Member.builder()
                .username("회원")
                .id(1L)
                .build();

        Long mockMemberId = 1L;

        setField(member, "id", mockMemberId);

        ReviewDTO.Add dto = ReviewDTO.Add.builder()
                .content("리뷰내용")
                .rating(5)
                .memberId(mockMemberId)
                .storeId(mockStoreId)
                .build();

        given(memberRepository.findById(mockMemberId)).willReturn(Optional.of(member));
        given(storeRepository.findWithOptimisticLockById(mockStoreId)).willReturn(Optional.of(store));
        given(reviewRepository.save(any())).willReturn(dto.toEntity());

        // when
        Review review = reviewService.save(dto, "");

        // then
        assertThat(dto.getContent()).isEqualTo(review.getContent());
        assertThat(dto.getRating()).isEqualTo(review.getRating());
        then(storeRepository).should().findWithOptimisticLockById(mockStoreId);
        then(memberRepository).should().findById(mockMemberId);
        then(reviewRepository).should().save(any());
    }

    @Test
    @DisplayName("리뷰 삭제")
    void should_DeleteReview_When_ByReviewId() throws IOException {
        // given
        Store store = Store.builder()
                .name("매장")
                .build();

        Long mockStoreId = 2L;

        setField(store, "id", mockStoreId);

        ReviewDTO.Add dto = ReviewDTO.Add.builder()
                .content("리뷰내용")
                .rating(5)
                .storeId(mockStoreId)
                .build();

        Long mockReviewId = 2L;

        setField(dto, "reviewId", mockReviewId);

        Review entity = dto.toEntity();

        UploadFile uploadFile1 = new UploadFile("업로드명1", "저장명1", "URL1");
        List<UploadFile> fileList = new ArrayList<>();
        fileList.add(uploadFile1);
        setField(entity, "fileList", fileList);

        given(reviewRepository.findById(mockReviewId)).willReturn(Optional.of(entity));
        given(storeRepository.findWithOptimisticLockById(mockStoreId)).willReturn(Optional.of(store));
        willDoNothing().given(awsS3FileStore).deleteFile(uploadFile1.getStoredFileName());

        // when
        Long reviewId = reviewService.delete(mockStoreId, mockReviewId);

        // then
        assertThat(dto.getReviewId()).isEqualTo(reviewId);
        then(reviewRepository).should().findById(reviewId);
        then(storeRepository).should().findWithOptimisticLockById(mockStoreId);
        then(reviewRepository).should().delete(entity);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 reviewId로 조회 시 예외 발생")
    void should_ThrowException_When_ExistsNotReviewById() {
        // given
        Long mockReviewId = 1L;
        given(reviewRepository.findById(1L)).willThrow(new IllegalArgumentException());

        // then
        assertThatThrownBy(() -> reviewService.findById(mockReviewId)).isInstanceOf(IllegalArgumentException.class);
        then(reviewRepository).should().findById(mockReviewId);
    }

    @Test
    @DisplayName("리뷰 등록시 별점 업데이트")
    void should_UpdateRating_When_BySaveReview() {
        // given
        Store store1 = Store.builder()
                .name("매장")
                .build();

        Long mockStoreId = 2L;

        setField(store1, "id", mockStoreId);

        Store store2 = Store.builder()
                .id(mockStoreId)
                .name("매장")
                .build();

        setField(store2, "ratingTotal", 5);

        ReviewDTO.Add dto = ReviewDTO.Add.builder()
                .content("리뷰내용")
                .rating(5)
                .storeId(mockStoreId)
                .build();

        Long mockReviewId = 2L;

        setField(dto, "reviewId", mockReviewId);

        Review entity = dto.toEntity();

        given(storeRepository.findWithOptimisticLockById(mockStoreId)).willReturn(Optional.of(store1));
        given(storeRepository.saveAndFlush(any())).willReturn(store2);

        // when
        Store result = reviewService.updateStoreRating(mockStoreId, entity, true);

        // then
        assertThat(store2.getRatingTotal()).isEqualTo(result.getRatingTotal());
        then(storeRepository).should().findWithOptimisticLockById(mockStoreId);
        then(storeRepository).should().saveAndFlush(any());

    }

    @Test
    @DisplayName("이미지URL 가져오기")
    void should_FindImgURL_When_ByReviewId() {
        // given
        ReviewMemberDTO dto = ReviewMemberDTO.builder()
                .storeName("가게")
                .build();

        Long mockReviewId = 3L;
        setField(dto, "reviewId", mockReviewId);

        List<String> imgUrl = new ArrayList<>();
        imgUrl.add("URL1");
        imgUrl.add("URL2");

        dto.setUploadImgUrl(imgUrl);

        List<ReviewMemberDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);

        given(reviewRepository.reviewImageUrl(mockReviewId)).willReturn(imgUrl);

        // when
        ReviewMemberDTO reviewMemberDTO = reviewService.findImg(dtoList).get(0);

        // then
        assertThat(reviewMemberDTO.getUploadImgUrl().size()).isEqualTo(imgUrl.size());
        then(reviewRepository).should().reviewImageUrl(mockReviewId);

    }


}
