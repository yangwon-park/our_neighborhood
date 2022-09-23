package ywphsm.ourneighbor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.dto.MenuDTO;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;

import javax.persistence.EntityManager;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ywphsm.ourneighbor.domain.QMenu.*;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
@Transactional
class MenuServiceTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    MenuService menuService;

    @Autowired
    StoreService storeService;

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void before() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("메뉴 등록")
    void saveMenu() throws Exception {
        String name = "test";
        Integer price = 10000;
        Long storeId = 24L;

        MenuDTO.Add dto = MenuDTO.Add.builder()
                .name(name)
                .price(price)
                .storeId(storeId)
                .build();

        String url = "http://localhost:" + port + "/menu/add";

        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png",
                new FileInputStream("/Users/bag-yang-won/Desktop/file/06d24685-63b7-49c9-99ae-db5e30957eed.jpg"));

        mvc.perform(multipart("/menu/add")
                        .file(file).part(new MockPart("id", "foo".getBytes(StandardCharsets.UTF_8)))
                        .param("storeId", String.valueOf(storeId))
                        .param("name", name)
                        .param("price", String.valueOf(price)))
                .andExpect(status().isOk());

        Store one = storeService.findOne(storeId);
        List<Menu> menuList = one.getMenuList();


        for (Menu menu : menuList) {
            if (menu.getName().equals(name)) {
                assertThat(menu.getPrice()).isEqualTo(10000);
                assertThat(menu.getFile().getUploadedFileName()).isEqualTo("test.png");
            }
        }
    }


    @Test
    @DisplayName("한 매장의 모든 메뉴 불러오기")
    void findAllMenuInAStore() {
        List<Store> stores = storeService.findStores();

        for (Store store : stores) {
            List<Menu> menuList = findMenuList(store);

            for (Menu menu : menuList) {
                if (menu.getStore().getId().equals(store.getId())) {
                    assertThat(store.getName()).isEqualTo(menu.getStore().getName());
                    System.out.println("메뉴 이름 : " + menu.getName());
                }
            }
        }
    }

    // 한 매장 내의 메뉴 불러오는 메소드
    private List<Menu> findMenuList(Store store) {
        return queryFactory
                .select(menu)
                .from(menu)
                .where(menu.store.id.eq(store.getId()))
                .fetch();
    }
}