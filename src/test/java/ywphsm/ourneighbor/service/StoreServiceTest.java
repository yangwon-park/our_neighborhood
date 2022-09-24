package ywphsm.ourneighbor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.domain.CategoryOfStore;
import ywphsm.ourneighbor.domain.embedded.Address;
import ywphsm.ourneighbor.domain.Category;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.store.*;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback(value = true)
class StoreServiceTest {

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    EntityManager em;

    @Autowired
    StoreService storeService;

    // Querydsl 사용
    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("매장 등록")
    void saveStore() throws Exception {
//      https://stackoverflow.com/questions/45044021/spring-mockmvc-request-parameter-list => 참고

        List<String> off = new ArrayList<>();

        off.add("일");
        off.add("토");

        List<String> categoryId = new ArrayList<>();

        categoryId.add("4");
        categoryId.add("6");
        categoryId.add("13");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("offDays", off);
        params.addAll("categoryId", categoryId);

        StoreDTO.Add dto = StoreDTO.Add.builder()
                .name("테크노할인마트")
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

        mvc.perform(multipart("/store/add")
                        .param("name", dto.getName())
                        .param("zipcode", dto.getZipcode())
                        .param("roadAddr", dto.getRoadAddr())
                        .param("numberAddr", dto.getNumberAddr())
                        .param("detail", dto.getDetail())
                        .param("lat", String.valueOf(dto.getLat()))
                        .param("lon", String.valueOf(dto.getLon()))
                        .param("phoneNumber", dto.getPhoneNumber())
                        .param("openingTIme", dto.getOpeningTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .param("closingTime", dto.getClosingTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .params(params))
                .andDo(print())
                .andExpect(status().isOk());

        // API 호출로 저장된 store 불러옴
        Store findStore = storeService.searchByKeyword(dto.getName()).get(0);

        assertThat(findStore.getName()).isEqualTo(dto.getName());

        // 설정된 카테고리의 개수 확인
        assertThat(findStore.getCategoryOfStoreList().size()).isEqualTo(3);

        // 카테고리가 알맞게 설정됐는지 확인
        for (int i = 0; i < findStore.getCategoryOfStoreList().size(); i++) {
            String id = categoryId.get(i);

            assertThat(findStore.getCategoryOfStoreList().get(i).getCategory().getId()).isEqualTo(Long.parseLong(id));
        }
    }
//
//    // 쉬는 날이 있는 가게 등록 한 후 다시 구현
//    @Test
//    @DisplayName("현재 요일 구하기")
//    void today() {
//        LocalDate date = LocalDate.now();
//        String today = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
//
//        LocalTime time = LocalTime.now();
//
//        Store findStore = storeService.searchByKeyword("맥도").get(0);
//
//        List<String> offDays = findStore.getOffDays();
//
//
//    }







}