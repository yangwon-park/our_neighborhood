package ywphsm.ourneighbor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(webEnvironment = SpringBootTest
        .WebEnvironment.RANDOM_PORT)
class CategoryServiceTest {


    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @LocalServerPort
    private int port;

    @Autowired
    CategoryService categoryService;

    @Test
    @DisplayName("카테고리 등록")
    void save() {
//        CategoryDTO cate1 = CategoryAddDTO.builder()
//                .name("전자 제품")
//                .depth(1L)
//                .parentName(null)
//                .build();
//        CategoryDTO cate2 = CategoryAddDTO.builder()
//                .name("컴퓨터")
//                .depth(2L)
//                .parentName(cate1.getParentName())
//                .build();
//
//        categoryService.saveCategory(cate1);
//        categoryService.saveCategory(cate2);
//        List<CategoryDTO> allCategories = categoryService.findAllCategories();
//
//        for (CategoryDTO allCategory : allCategories) {
//            System.out.println("allCategory = " + allCategory);
//        }
    }

}