package ywphsm.ourneighbor.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.member.Member;
import ywphsm.ourneighbor.domain.menu.Menu;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.repository.member.MemberRepository;
import ywphsm.ourneighbor.service.login.SessionConst;

import java.io.FileInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class MenuServiceTest {

    @Autowired
    private WebApplicationContext context;

    MockHttpSession session;

    private MockMvc mvc;

    @Autowired
    MenuService menuService;

    @Autowired
    StoreService storeService;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    public Member login(String userId, String password) {
        return memberRepository.findByUserId(userId)
                .filter(member -> passwordEncoder.matches(password, member.getPassword()))
                .orElse(null);

    }

    @BeforeEach
    void before() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Member loginMember = login("arnold1998", "Arnold!(97");
        session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }



    @Test
    @WithMockUser(username = "ADMIN", roles = "ADMIN")
    @DisplayName("메뉴 등록")
    void save() throws Exception {
        Long storeId = 24L;
        String name = "test";
        Integer price = 10000;

//        new MockMultipartFile("필드명", storedFileName, contentType, 서버에 있는 파일 경로)
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png",
//                new FileInputStream("C:/Users/ywOnp/Desktop/Study/review/file/761c40d5-8fae-4d40-85b8-a26d10a6e52c.png"));
                new FileInputStream("C:/Users/HOME/Desktop/JAVA/menu_file/5cf53790-54a5-4c5f-9709-0394d58cec94.png"));
//                new FileInputStream("/Users/bag-yang-won/Desktop/file/ad9e8baf-5293-4403-b796-fb59a6f0c317.jpg"));

        JSONObject json = new JSONObject();

        json.put("value", "해쉬태그1");
        json.put("value", "해쉬태그2");

        JSONArray array = new JSONArray();
        array.add(json);

        mvc.perform(multipart("/seller/menu")
                        .file(file).session(session)
                        .param("storeId", String.valueOf(storeId))
                        .param("name", name)
                        .param("price", String.valueOf(price))
                        .param("hashtag", String.valueOf(array)))
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
    void update() throws Exception {
        Long storeId = 24L;
        Long menuId = 970L;

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
        Long storeId = 24L;
        Long menuId = 970L;

        String url = "http://localhost" + port + "/seller/menu/" + storeId;

        mvc.perform(delete(url)
                        .param("menuId", String.valueOf(menuId)))
                .andDo(print())
                .andExpect(status().isOk());

        assertThatThrownBy(() -> menuService.findById(menuId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}