package ywphsm.ourneighbor.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.domain.Menu;
import ywphsm.ourneighbor.domain.store.Store;

import javax.persistence.EntityManager;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        Long storeId = 24L;
        String name = "test";
        Integer price = 10000;

        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png",
                new FileInputStream("C:/Users/ywOnp/Desktop/Study/review/file/ad9e8baf-5293-4403-b796-fb59a6f0c317.jpg"));

        mvc.perform(multipart("/menu/add")
                        .file(file).part(new MockPart("id", "foo".getBytes(StandardCharsets.UTF_8)))
                        .param("storeId", String.valueOf(storeId))
                        .param("name", name)
                        .param("price", String.valueOf(price)))
                .andDo(print())
                .andExpect(status().isOk());

        List<Menu> menuList = storeService.findById(storeId).getMenuList();


        for (Menu menu : menuList) {
            if (menu.getName().equals(name)) {
                assertThat(menu.getPrice()).isEqualTo(10000);
                assertThat(menu.getFile().getUploadedFileName()).isEqualTo("test.png");
            }
        }
    }

}