package ywphsm.ourneighbor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ywphsm.ourneighbor.domain.dto.CategoryAddDTO;
import ywphsm.ourneighbor.domain.dto.CategoryDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        CategoryAddDTO cate1 = CategoryAddDTO.builder()
                .name("전자 제품")
                .depth(1L)
                .parent_id(null)
                .build();
        CategoryAddDTO cate2 = CategoryAddDTO.builder()
                .name("컴퓨터")
                .depth(2L)
                .parent_id(14L)
                .build();

        categoryService.saveCategory(cate1);
        categoryService.saveCategory(cate2);
        List<CategoryDTO> allCategories = categoryService.findAllCategories();

        for (CategoryDTO allCategory : allCategories) {
            System.out.println("allCategory = " + allCategory);
        }
    }

}