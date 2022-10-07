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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.dto.StoreDTO;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.menu.MenuType;
import ywphsm.ourneighbor.domain.menu.QMenu;
import ywphsm.ourneighbor.domain.store.*;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ywphsm.ourneighbor.domain.menu.QMenu.menu;
import static ywphsm.ourneighbor.domain.store.QStore.store;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
@Transactional
class StoreServiceTest {

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    StoreService storeService;

    @Autowired
    CategoryService categoryService;

    @LocalServerPort
    private int port;

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);

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
    }

    @Test
    @WithMockUser(username = "seller", roles = "SELLER")
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

        mvc.perform(multipart("/seller/store")
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
                        .params(params))
                .andDo(print())
                .andExpect(status().isOk());

        // API 호출로 저장된 store 불러옴
        Store findStore = storeService.searchByKeyword(dto.getName()).get(0);

        assertThat(findStore.getName()).isEqualTo(dto.getName());

        assertThat(findStore.getOffDays().get(0)).isEqualTo("일");

        // 설정된 카테고리의 개수 확인
        assertThat(findStore.getCategoryOfStoreList().size()).isEqualTo(3);

        // 카테고리가 알맞게 설정됐는지 확인
        for (int i = 0; i < findStore.getCategoryOfStoreList().size(); i++) {
            String id = categoryId.get(i);

            assertThat(findStore.getCategoryOfStoreList().get(i).getCategory().getId()).isEqualTo(Long.parseLong(id));
        }
    }

    @Test
    @WithMockUser(username = "seller", roles = "SELLER")
    @DisplayName("매장 수정")
    void updateStore() throws Exception {

        List<String> off = new ArrayList<>();
        off.add("일");
        off.add("토");

        List<Long> categoryId = new ArrayList<>();
        categoryId.add(4L);
        categoryId.add(6L);
        categoryId.add(13L);

        List<Category> categoryList = new ArrayList<>();

        for (Long id : categoryId) {
            categoryList.add(categoryService.findById(id));
        }

        Long storeId = storeService.save(StoreDTO.Add.builder()
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
                .build(), categoryList);

        List<String> updateCategoryId = new ArrayList<>();
        updateCategoryId.add("4");
        updateCategoryId.add("7");
        updateCategoryId.add("12");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("categoryId", updateCategoryId);
        params.addAll("offDays", off);

        StoreDTO.Update dto = StoreDTO.Update.builder()
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

        mvc.perform(multipart("/seller/store/" + storeId)
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
                        .params(params)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andDo(print())
                .andExpect(status().isOk());

        Store findStore = storeService.searchByKeyword(dto.getName()).get(0);

        assertThat(findStore.getName()).isEqualTo(dto.getName());

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
        List<Long> categoryId = new ArrayList<>();

        categoryId.add(4L);
        categoryId.add(6L);
        categoryId.add(13L);

        List<Category> categoryList = new ArrayList<>();

        for (Long id : categoryId) {
            categoryList.add(categoryService.findById(id));
        }

        Long storeId = storeService.save(StoreDTO.Add.builder()
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
                .build(), categoryList);

        String url = "http://localhost:" + port + "/admin/store/" + storeId;

        mvc.perform(delete(url))
                .andDo(print())
                .andExpect(status().isOk());

        assertThatThrownBy(() -> storeService.findById(storeId))
                .isInstanceOf(IllegalArgumentException.class);
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