package ywphsm.ourneighbor.service;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.domain.store.*;
import ywphsm.ourneighbor.repository.store.dto.SimpleSearchStoreDTO;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ywphsm.ourneighbor.domain.menu.QMenu.menu;
import static ywphsm.ourneighbor.domain.store.distance.Distance.calculateHowFarToTheTarget;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class StoreServiceTest {

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @LocalServerPort
    private int port;

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    MockHttpSession session;

    @Autowired
    MemberService memberService;

    @Autowired
    StoreService storeService;

    @Autowired
    CategoryService categoryService;


    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);

        Member member = memberService.findByUserId("ywonp94");

        session = new MockHttpSession();
        session.setAttribute("loginMember", member);

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("테스트")
    void 테스트_중() {
        NumberExpression<Integer> typeRank = new CaseBuilder()
                .when(menu.type.eq(MenuType.MAIN)).then(1)
                .when(menu.type.eq(MenuType.DESSERT)).then(2)
                .when(menu.type.eq(MenuType.BEVERAGE)).then(3)
                .when(menu.type.eq(MenuType.DRINK)).then(4)
                .otherwise(5);

        List<Menu> list = queryFactory
                .selectFrom(menu)
                .where(menu.store.id.eq(24L))
                .orderBy(typeRank.asc())
                .fetch();

        for (Menu menu : list) {
            System.out.println("menu = " + menu);
        }

        List<Store> aalist = storeService.findAllStores(1000000L);
        System.out.println("aalist.size() = " + aalist.size());
        for (Store store : aalist) {
            System.out.println("store.getLat() = " + store.getLat());
            System.out.println("store.getLon() = " + store.getLon());
            System.out.println("store.getPoint() = " + store.getPoint());
        }

    }

    @Test
    @WithMockUser(username = "seller", roles = "SELLER")
    @DisplayName("매장 등록 및 수정")
    void saveStore() throws Exception {
//      https://stackoverflow.com/questions/45044021/spring-mockmvc-request-parameter-list => 참고

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

        ResultActions resultActions = mvc.perform(multipart("/seller/store")
                        .param("name", dto.getName())
                        .param("zipcode", dto.getZipcode())
                        .param("roadAddr", dto.getRoadAddr())
                        .param("numberAddr", dto.getNumberAddr())
                        .param("detail", dto.getDetail())
                        .param("lat", String.valueOf(dto.getLat()))
                        .param("lon", String.valueOf(dto.getLon()))
                        .param("phoneNumber", dto.getPhoneNumber())
                        .param("openingTime", dto.getOpeningTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .param("closingTime", dto.getClosingTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .param("memberId", String.valueOf(453))
                        .params(params))
                .andDo(print())
                .andExpect(status().isOk());

        // C, U, D가 일어나면 Auditing에서 에러 발생
        // 원인은 알겠으나 해결법을 아직 모름 => SpringBootTest의 기능으로만 테스트 구현하면 성공하므로 일단 이렇게 대처
        // 저장된 store 불러옴
//        Store findStore = storeService.searchByKeyword(dto.getName()).get(0);

        // API 호출의 Return 값인 Id를 구하기 위한 로직
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Long storeId = Long.parseLong(response.getContentAsString());

        Store findStore = storeService.findById(storeId);

        assertThat(findStore.getName()).isEqualTo(dto.getName());

        assertThat(findStore.getOffDays().get(0)).isEqualTo("월");

        // 설정된 카테고리의 개수 확인
        assertThat(findStore.getCategoryOfStoreList().size()).isEqualTo(3);

        // 카테고리가 알맞게 설정됐는지 확인
        for (int i = 0; i < findStore.getCategoryOfStoreList().size(); i++) {
            String id = categoryId.get(i);

            assertThat(findStore.getCategoryOfStoreList().get(i).getCategory().getId()).isEqualTo(Long.parseLong(id));
        }


        List<String> updateOff = new ArrayList<>();
        updateOff.add("일");
        updateOff.add("토");

        List<String> updateCategoryId = new ArrayList<>();
        updateCategoryId.add("4");
        updateCategoryId.add("7");
        updateCategoryId.add("12");

        MultiValueMap<String, String> updateParams = new LinkedMultiValueMap<>();
        updateParams.addAll("categoryId", updateCategoryId);
        updateParams.addAll("offDays", updateOff);

        StoreDTO.Update updateDTO = StoreDTO.Update.builder()
                .name("할인마트")
                .zipcode("54321")
                .roadAddr("우리집")
                .numberAddr("아파트")
                .detail("좋아요")
                .lat(123.456)
                .lon(35.678)
                .phoneNumber("0123456789")
                .openingTime(LocalTime.now())
                .closingTime(LocalTime.now())
                .build();

//        https://stackoverflow.com/questions/62862635/mockmvc-calling-a-put-endpoint-that-accepts-a-multipart-file
//        MulitipartFormData의 경우 기본적으로 request method가 POST로 지정돼있음
//        아래의 코드로 method를 바꿔줄 수 있음
//        더 최신 스프링 (5.3.17) 에서부터는 perform 내의 with 메소드를 사용하여 간단하게 바꿔줄 수 있음
//        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/store/edit" + storeId);
//        builder.with(request -> {
//            request.setMethod("PUT");
//            return request;
//        });

        mvc.perform(multipart("/seller/store/" + storeId).session(session)
                        .param("name", updateDTO.getName())
                        .param("zipcode", updateDTO.getZipcode())
                        .param("roadAddr", updateDTO.getRoadAddr())
                        .param("numberAddr", updateDTO.getNumberAddr())
                        .param("detail", updateDTO.getDetail())
                        .param("lat", String.valueOf(updateDTO.getLat()))
                        .param("lon", String.valueOf(updateDTO.getLon()))
                        .param("phoneNumber", updateDTO.getPhoneNumber())
                        .param("openingTime", updateDTO.getOpeningTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .param("closingTime", updateDTO.getClosingTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .params(updateParams)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andDo(print())
                .andExpect(status().isOk());


        assertThat(findStore.getName()).isEqualTo(updateDTO.getName());

        // 설정된 카테고리의 개수 확인
        assertThat(findStore.getCategoryOfStoreList().size()).isEqualTo(3);

        // 카테고리가 알맞게 설정됐는지 확인
        for (int i = 0; i < findStore.getCategoryOfStoreList().size(); i++) {
            String id = updateCategoryId.get(i);

            assertThat(findStore.getCategoryOfStoreList().get(i).getCategory().getId()).isEqualTo(Long.parseLong(id));
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("매장 삭제")
    void deleteStore() throws Exception {
        Long storeId = 24L;

        String url = "http://localhost:" + port + "/admin/store/" + storeId;

        mvc.perform(delete(url))
                .andDo(print())
                .andExpect(status().isOk());

        assertThatThrownBy(() -> storeService.findById(storeId))
                .isInstanceOf(IllegalArgumentException.class);
    }




}