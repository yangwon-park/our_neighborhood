package ywphsm.ourneighbor.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import ywphsm.ourneighbor.domain.dto.RequestAddStoreDTO;
import ywphsm.ourneighbor.domain.dto.ReviewMemberDTO;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.embedded.BusinessTime;
import ywphsm.ourneighbor.domain.file.UploadFile;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.member.Role;
import ywphsm.ourneighbor.domain.store.RequestAddStore;
import ywphsm.ourneighbor.domain.store.Review;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.repository.requestaddstore.RequestAddStoreRepository;

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
public class RequestAddStoreRepositoryTest {

    @Autowired
    TestEntityManager tem;

    @Autowired
    RequestAddStoreRepository requestAddStoreRepository;

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

        RequestAddStore requestAddStore1 = RequestAddStore.builder()
                .name("가게1")
                .address(new Address("테스트 로1", "11111", "48017", "상세 주소"))
                .content("가게요청1")
                .member(member1)
                .build();

        RequestAddStore requestAddStore2 = RequestAddStore.builder()
                .name("가게2")
                .address(new Address("테스트 로2", "22222", "48017", "상세 주소"))
                .content("가게요청2")
                .member(member1)
                .build();

        RequestAddStore requestAddStore3 = RequestAddStore.builder()
                .name("가게3")
                .address(new Address("테스트 로3", "33333", "48017", "상세 주소"))
                .content("가게요청3")
                .member(member1)
                .build();

        RequestAddStore requestAddStore4 = RequestAddStore.builder()
                .name("가게4")
                .address(new Address("테스트 로4", "44444", "48017", "상세 주소"))
                .content("가게요청4")
                .member(member1)
                .build();

        RequestAddStore requestAddStore5 = RequestAddStore.builder()
                .name("가게5")
                .address(new Address("테스트 로5", "55555", "48017", "상세 주소"))
                .content("가게요청5")
                .member(member1)
                .build();

        tem.persist(requestAddStore1);
        tem.persist(requestAddStore2);
        tem.persist(requestAddStore3);
        tem.persist(requestAddStore4);
        tem.persist(requestAddStore5);

    }

    @Test
    @DisplayName("가게추가 요청 저장")
    void should_SaveARequestAddStore() {
        RequestAddStore requestAddStore = RequestAddStore.builder()
                .name("가게1")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .content("가게요청1")
                .build();


        RequestAddStore savedRequestAddStore = requestAddStoreRepository.save(requestAddStore);

        assertThat(savedRequestAddStore.getContent()).isEqualTo(requestAddStore.getContent());
    }

    @Test
    @DisplayName("가게추가 요청 삭제시 IsEmpty 확인")
    void should_IsEmpty_When_DeleteARequestAddStore() {
        RequestAddStore requestAddStore = RequestAddStore.builder()
                .name("가게1")
                .address(new Address("테스트 로", "11111", "48017", "상세 주소"))
                .content("가게요청1")
                .build();

        tem.persist(requestAddStore);

        requestAddStoreRepository.deleteById(requestAddStore.getId());

        Optional<RequestAddStore> findRequestAddStore = requestAddStoreRepository.findById(requestAddStore.getId());

        assertThat(findRequestAddStore).isEmpty();
    }

    @Test
    @DisplayName("가게추가 요청 페이징으로 조회")
    void should_findRequestAddStore_When_Pageable() {

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<RequestAddStoreDTO.Detail> requestAddStorePage = requestAddStoreRepository.requestAddStorePage(pageRequest);

        assertThat(requestAddStorePage.hasNext()).isFalse();
        assertThat(requestAddStorePage.getSize()).isEqualTo(5);
        assertThat(requestAddStorePage.getNumber()).isEqualTo(0);

    }

}
