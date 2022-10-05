package ywphsm.ourneighbor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.menu.Menu;
import ywphsm.ourneighbor.domain.store.Store;
import ywphsm.ourneighbor.domain.store.StoreStatus;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.domain.dto.MenuDTO;

import java.io.FileInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @DisplayName("메뉴 등록")
    void saveMenu() throws Exception {
        Long storeId = 24L;
        String name = "test";
        Integer price = 10000;

//        new MockMultipartFile("필드명", storedFileName, contentType, 서버에 있는 파일 경로)
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png",
                new FileInputStream("C:/Users/ywOnp/Desktop/Study/review/file/761c40d5-8fae-4d40-85b8-a26d10a6e52c.png"));
//                new FileInputStream("/Users/bag-yang-won/Desktop/file/ad9e8baf-5293-4403-b796-fb59a6f0c317.jpg"));

        mvc.perform(multipart("/seller/menu")
                        .file(file)
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

    @Test
    @WithMockUser(username = "seller1", roles = "SELLER")
    @DisplayName("메뉴 수정")
    void updateMenu() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png",
                new FileInputStream("C:/Users/ywOnp/Desktop/Study/review/file/761c40d5-8fae-4d40-85b8-a26d10a6e52c.png"));
//                new FileInputStream("/Users/bag-yang-won/Desktop/file/ad9e8baf-5293-4403-b796-fb59a6f0c317.jpg"));

        Long storeId = 24L;

        MenuDTO.Add dto = MenuDTO.Add.builder()
                .storeId(storeId)
                .name("menu")
                .price(10000)
                .file(file)
                .build();

        Long menuId = menuService.save(dto);

        System.out.println("menuId = " + menuId);

        MockMultipartFile newFile = new MockMultipartFile("file", "new.png", "image/png",
                new FileInputStream("C:/Users/ywOnp/Desktop/Study/review/file/761c40d5-8fae-4d40-85b8-a26d10a6e52c.png"));
//                new FileInputStream("/Users/bag-yang-won/Desktop/file/ad9e8baf-5293-4403-b796-fb59a6f0c317.jpg"));


        mvc.perform(multipart("/seller/menu/" + storeId)
                        .file(newFile)
                        .param("id", String.valueOf(menuId))
                        .param("storeId", String.valueOf(storeId))
                        .param("name", "new")
                        .param("price", String.valueOf(12000))
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andDo(print())
                .andExpect(status().isOk());

        Menu menu = menuService.findById(menuId);
        assertThat(menu.getFile().getUploadedFileName()).isEqualTo("new.png");
        assertThat(menu.getName()).isEqualTo("new");
        assertThat(menu.getPrice()).isEqualTo(12000);
        assertThat(menu.getStore().getId()).isEqualTo(24L);
    }

    @Test
    @WithMockUser(username = "seller1", roles = "SELLER")
    @DisplayName("메뉴 삭제")
    void deleteMenu() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "test.png", "image/png",
                new FileInputStream("C:/Users/ywOnp/Desktop/Study/review/file/761c40d5-8fae-4d40-85b8-a26d10a6e52c.png"));
//                new FileInputStream("/Users/bag-yang-won/Desktop/file/ad9e8baf-5293-4403-b796-fb59a6f0c317.jpg"));

        Long storeId = 24L;

        MenuDTO.Add dto = MenuDTO.Add.builder()
                .storeId(storeId)
                .name("menu")
                .price(10000)
                .file(file)
                .build();

        Long menuId = menuService.save(dto);

        String url = "http://localhost" + port + "/seller/menu/" + storeId;

        mvc.perform(delete(url)
                        .param("menuId", String.valueOf(menuId)))
                .andDo(print())
                .andExpect(status().isOk());

        assertThatThrownBy(() -> menuService.findById(menuId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}