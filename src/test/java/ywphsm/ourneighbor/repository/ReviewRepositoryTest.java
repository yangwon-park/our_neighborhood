package ywphsm.ourneighbor.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ywphsm.ourneighbor.domain.dto.ReviewDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.repository.review.ReviewRepository;
import ywphsm.ourneighbor.repository.store.StoreRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    StoreRepository storeRepository;

    @Test
    public void 리뷰페이징_2개() throws IOException {
        Member member = memberRepository.save(new Member("username", "991002", 20, "01011111111",
                0, "beforeId", "password",
                "testEmail@email.com", "testNickname", Role.USER));

        List<String> off = new ArrayList<>();
        off.add("월");
        off.add("화");
        List<String> categoryId = new ArrayList<>();
        categoryId.add("4");
        categoryId.add("6");
        categoryId.add("13");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("offDays", off);
        params.addAll("categoryId", categoryId);
        StoreDTO.Add dto = StoreDTO.Add.builder()
                .name("aa22sdasa")
                .zipcode("12345")
                .roadAddr("우리집")
                .numberAddr("아파트")
                .detail("좋아요")
                .lat(123.456)
                .lon(35.678)
                .phoneNumber("0123456789")
                .openingTime(LocalTime.now())
                .closingTime(LocalTime.now())
                .build();

        Store store = storeRepository.save(dto.toEntity());

//        new MockMultipartFile("필드명", storedFileName, contentType, 서버에 있는 파일 경로)
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png",
//                new FileInputStream("C:/Users/ywOnp/Desktop/Study/review/file/785c984e-fab2-4422-9833-646d94b631ae.jpg"));
                new FileInputStream("C:/Users/HOME/Desktop/JAVA/menu_file/5cf53790-54a5-4c5f-9709-0394d58cec94.png"));
//                new FileInputStream("/Users/bag-yang-won/Desktop/file/ad9e8baf-5293-4403-b796-fb59a6f0c317.jpg"));
        reviewRepository.save(new ReviewDTO.Add("Test Content", 5, store.getId(), member.getId(), file)
                .toEntity(store, member));
        reviewRepository.save(new ReviewDTO.Add("Test Content", 5, store.getId(), member.getId(), file)
                .toEntity(store, member));
        reviewRepository.save(new ReviewDTO.Add("Test Content", 5, store.getId(), member.getId(), file)
                .toEntity(store, member));
        reviewRepository.save(new ReviewDTO.Add("Test Content", 5, store.getId(), member.getId(), file)
                .toEntity(store, member));
        reviewRepository.save(new ReviewDTO.Add("Test Content", 5, store.getId(), member.getId(), file)
                .toEntity(store, member));

        PageRequest pageRequest = PageRequest.of(0, 4);
        Slice<ReviewMemberDTO> result = reviewRepository.reviewPage(pageRequest, store.getId());
        List<ReviewMemberDTO> resultList = new ArrayList<>();
        for (ReviewMemberDTO reviewMemberDTO : result) {
            resultList.add(reviewMemberDTO);
        }
        ReviewMemberDTO reviewMemberDTO = resultList.get(0);

        assertThat(result.getContent().size()).isEqualTo(4);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.hasNext()).isTrue();
        assertThat(reviewMemberDTO.getContent()).isEqualTo("Test Content");
        assertThat(reviewMemberDTO.getRating()).isEqualTo(5);

        //MyPage
        List<ReviewMemberDTO> reviewMemberDTOS = reviewRepository.myReview(member.getId());
        assertThat(reviewMemberDTOS.size()).isEqualTo(5);
        assertThat(reviewMemberDTOS.get(0).getContent()).isEqualTo("Test Content");
        assertThat(reviewMemberDTOS.get(0).getRating()).isEqualTo(5);
    }
}