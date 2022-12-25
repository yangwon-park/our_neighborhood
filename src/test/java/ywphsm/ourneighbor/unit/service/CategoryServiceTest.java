package ywphsm.ourneighbor.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ywphsm.ourneighbor.domain.category.Category;
import ywphsm.ourneighbor.domain.dto.category.CategoryDTO;
import ywphsm.ourneighbor.repository.category.CategoryRepository;
import ywphsm.ourneighbor.service.CategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.util.ReflectionTestUtils.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;

    @BeforeEach
    void beforeEach() {

    }

    @Test
    @DisplayName("부모 카테고리가 없고, 기존에 ROOT 카테고리도 없는 경우에 카테고리 등록")
    void should_FindCategory_When_ParentIdIsNullAndRootCategoryNotExists() {
        /*
            이 테스트 케이스는 최초로 카테고리를 등록하는 경우
         */
        
        // given
        Category root = Category.builder()
                .name("ROOT")
                .depth(0L)
                .build();

        CategoryDTO.Detail dto = CategoryDTO.Detail.builder()
                .name("카테고리")
                .parent_id(null)
                .depth(null)
                .build();

        Category entity = dto.toEntity();

        given(categoryRepository.save(any())).willReturn(entity);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(entity));
        given(categoryRepository.findByNameAndDepth("ROOT", 0L)).willReturn(Optional.ofNullable(root));
        given(categoryRepository.existsByNameAndDepth("ROOT", 0L)).willReturn(false);       // return false: ROOT가 기존에 없음

        // when
        categoryService.save(dto);
        Category findCategory = categoryService.findById(1L);

        // then
        // ROOT 카테고리도 등록해야 하므로 save가 2회 호출됨
        then(categoryRepository).should().findByNameAndDepth("ROOT", 0L);
        then(categoryRepository).should().existsByNameAndDepth("ROOT", 0L);
        then(categoryRepository).should().findById(1L);
        then(categoryRepository).should(times(2)).save(any());
        assertThat(entity.getName()).isEqualTo(findCategory.getName());
    }


    @Test
    @DisplayName("부모 카테고리가 없고, 기존에 ROOT 카테고리는 있는 경우에 카테고리 등록")
    void should_FindCategory_When_ParentIdIsNullAndRootCategoryExists() {
        // given
        Category root = Category.builder()
                .name("ROOT")
                .depth(0L)
                .build();

        CategoryDTO.Detail dto = CategoryDTO.Detail.builder()
                .name("카테고리")
                .parent_id(null)
                .depth(null)
                .build();

        Category entity = dto.toEntity();

        given(categoryRepository.save(any())).willReturn(entity);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(entity));
        given(categoryRepository.findByNameAndDepth("ROOT", 0L)).willReturn(Optional.ofNullable(root));
        given(categoryRepository.existsByNameAndDepth("ROOT", 0L)).willReturn(true);

        // when
        categoryService.save(dto);
        Category findCategory = categoryService.findById(1L);

        // then
        // ROOT 카테고리도 등록해야 하므로 save가 2회 호출됨
        then(categoryRepository).should().findByNameAndDepth("ROOT", 0L);
        then(categoryRepository).should().existsByNameAndDepth("ROOT", 0L);
        then(categoryRepository).should().findById(1L);
        then(categoryRepository).should(times(1)).save(any());
        assertThat(entity.getName()).isEqualTo(findCategory.getName());
    }

    @Test
    @DisplayName("부모 카테고리가 있는 카테고리 등록")
    void should_FindCategory_When_ParentIdIsNotNull() {
        // given
        CategoryDTO.Detail dto = CategoryDTO.Detail.builder()
                .name("카테고리")
                .parent_id(1L)
                .depth(2L)
                .build();

        Category entity = dto.toEntity();

        Category parent = Category.builder()
                .name("부모")
                .parent(null)
                .depth(1L)
                .children(new ArrayList<>())
                .build();

        Long parentMockId = 1L;
        Long mockId = 2L;

        setField(parent, "id", parentMockId);
        setField(entity, "id", mockId);

        given(categoryRepository.save(any())).willReturn(entity);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(parent));
        given(categoryRepository.findById(2L)).willReturn(Optional.of(entity));

        // when
        categoryService.save(dto);
        Category findParent = categoryService.findById(parentMockId);
        Category findCategory = categoryService.findById(mockId);

        // then
        then(categoryRepository).should().save(any());
        then(categoryRepository).should(times(2)).findById(parentMockId);           // Service 계층에서 조회 1번, when에서 조회 1번 => 총 2번
        then(categoryRepository).should(times(1)).findById(mockId);                 // 부모를 조회하는 경우는 when에서 1번만 발생하고 Service 자체에서는 수행되지 않음 => 총 1번

        assertThat(dto.getName()).isEqualTo(findCategory.getName());
        assertThat(parent.getName()).isEqualTo(findParent.getName());
    }

    @Test
    @DisplayName("categoryId로 카테고리 삭제")
    void should_DeleteCategory_When_ById() {
        // given
        CategoryDTO.Detail dto = CategoryDTO.Detail.builder()
                .name("카테고리")
                .parent_id(1L)
                .depth(2L)
                .build();

        Category entity = dto.toEntity();

        Long mockId = 1L;

        setField(entity, "id", mockId);

        given(categoryRepository.findById(mockId)).willReturn(Optional.of(entity));

        // when
        Long categoryId = categoryService.delete(mockId);

        // then
        then(categoryRepository).should().findById(mockId);
        then(categoryRepository).should().delete(entity);
        assertThat(entity.getId()).isEqualTo(categoryId);
    }

    @Test
    @DisplayName("categoryId로 카테고리 조회")
    void should_FindCategory_When_ById() {
        // given
        CategoryDTO.Detail dto = CategoryDTO.Detail.builder()
                .name("카테고리")
                .parent_id(null)
                .depth(null)
                .build();

        Category entity = dto.toEntity();
        Long mockId = 1L;

        setField(entity, "id", mockId);

        given(categoryRepository.findById(mockId)).willReturn(Optional.of(entity));

        // when
        Category findCategory = categoryService.findById(mockId);

        // then
        then(categoryRepository).should().findById(mockId);
        assertThat(entity.getName()).isEqualTo(findCategory.getName());
    }

    @Test
    @DisplayName("뎁스로 카테고리 조회 - 카테고리 이름마다 부과한 가중치에 대하여 정렬 수행")
    void should_FindCategories_When_ByDepthCaseByOrderByName() {
        // given
        Category category1 = Category.builder()
                .name("카테고리")
                .build();

        Category category2 = Category.builder()
                .name("카테고리2")
                .build();

        List<Category> list = new ArrayList<>();

        list.add(category1);
        list.add(category2);

        Long depth = 1L;

        given(categoryRepository.findByDepthCaseByOrderByName(depth)).willReturn(list);

        // when
        List<CategoryDTO.Simple> result = categoryService.findByDepthCaseByOrderByName(depth);

        // then
        then(categoryRepository).should().findByDepthCaseByOrderByName(depth);
        assertThat(result).hasSize(2).contains(CategoryDTO.Simple.of(category1), CategoryDTO.Simple.of(category2));
    }

    @Test
    @DisplayName("모든 카테고리를 조건에 맞게 정렬 수행하여 조회")
    void should_FineAllCategories_When_ByOrderByDepthAscParentIdAscNameAsc() {
        Category parent = Category.builder()
                .name("부모")
                .parent(null)
                .depth(1L)
                .children(new ArrayList<>())
                .build();

        // given
        CategoryDTO.Detail dto1 = CategoryDTO.Detail.builder()
                .name("카테고리1")
                .parent_id(1L)
                .depth(2L)
                .build();

        CategoryDTO.Detail dto2 = CategoryDTO.Detail.builder()
                .name("카테고리2")
                .parent_id(1L)
                .depth(2L)
                .build();

        CategoryDTO.Detail dto3 = CategoryDTO.Detail.builder()
                .name("카테고리3")
                .parent_id(2L)
                .depth(3L)
                .build();

        CategoryDTO.Detail dto4 = CategoryDTO.Detail.builder()
                .name("카테고리4")
                .parent_id(3L)
                .depth(3L)
                .build();

        CategoryDTO.Detail dto5 = CategoryDTO.Detail.builder()
                .name("카테고리5")
                .parent_id(2L)
                .depth(3L)
                .build();

        setField(dto1, "categoryId", 2L);
        setField(dto2, "categoryId", 3L);
        setField(dto3, "categoryId", 4L);
        setField(dto4, "categoryId", 5L);
        setField(dto5, "categoryId", 6L);

        Category category1 = dto1.toEntity();
        Category category2 = dto2.toEntity();
        Category category3 = dto3.toEntity();
        Category category4 = dto4.toEntity();
        Category category5 = dto5.toEntity();

        setField(parent, "id", 1L);
        setField(category1, "id", 2L);
        setField(category2, "id", 3L);
        setField(category3, "id", 4L);
        setField(category4, "id", 5L);
        setField(category5, "id", 6L);

        category1.addParentCategoryAndDepth(parent, parent.getDepth() +1);
        category2.addParentCategoryAndDepth(parent, parent.getDepth() +1);
        category3.addParentCategoryAndDepth(category1, category1.getDepth() +1);
        category4.addParentCategoryAndDepth(category2, category2.getDepth() +1);
        category5.addParentCategoryAndDepth(category1, category1.getDepth() +1);

        List<Category> list = new ArrayList<>();

        list.add(category1);
        list.add(category2);
        list.add(category3);
        list.add(category5);
        list.add(category4);

        given(categoryRepository.findAllByOrderByDepthAscParentIdAscNameAsc()).willReturn(list);

        // when
        List<CategoryDTO.Detail> dtoList = categoryService.findAllByOrderByDepthAscParentIdAscNameAsc();

        // then
        then(categoryRepository).should().findAllByOrderByDepthAscParentIdAscNameAsc();
        assertThat(dtoList).hasSize(5).containsExactly(dto1, dto2, dto3, dto5, dto4);
    }

    @Test
    @DisplayName("부모 카테고리를 통해서 모든 계층화된 카테고리 찾기")
    void should_FindAllCategoriesHierarchy() {
        // given
        Category parent = Category.builder()
                .name("부모")
                .parent(null)
                .depth(1L)
                .children(new ArrayList<>())
                .build();

        CategoryDTO.Detail dto1 = CategoryDTO.Detail.builder()
                .name("카테고리1")
                .parent_id(1L)
                .depth(2L)
                .build();

        CategoryDTO.Detail dto2 = CategoryDTO.Detail.builder()
                .name("카테고리2")
                .parent_id(1L)
                .depth(2L)
                .build();

        CategoryDTO.Detail dto3 = CategoryDTO.Detail.builder()
                .name("카테고리3")
                .parent_id(2L)
                .depth(3L)
                .build();

        CategoryDTO.Detail dto4 = CategoryDTO.Detail.builder()
                .name("카테고리4")
                .parent_id(3L)
                .depth(3L)
                .build();

        CategoryDTO.Detail dto5 = CategoryDTO.Detail.builder()
                .name("카테고리5")
                .parent_id(2L)
                .depth(3L)
                .build();

        setField(dto1, "categoryId", 2L);
        setField(dto2, "categoryId", 3L);
        setField(dto3, "categoryId", 4L);
        setField(dto4, "categoryId", 5L);
        setField(dto5, "categoryId", 6L);

        Category category1 = dto1.toEntity();
        Category category2 = dto2.toEntity();
        Category category3 = dto3.toEntity();
        Category category4 = dto4.toEntity();
        Category category5 = dto5.toEntity();

        setField(parent, "id", 1L);
        setField(category1, "id", 2L);
        setField(category2, "id", 3L);
        setField(category3, "id", 4L);
        setField(category4, "id", 5L);
        setField(category5, "id", 6L);

        category1.addParentCategoryAndDepth(parent, parent.getDepth() +1);
        category2.addParentCategoryAndDepth(parent, parent.getDepth() +1);
        category3.addParentCategoryAndDepth(category1, category1.getDepth() +1);
        category4.addParentCategoryAndDepth(category2, category2.getDepth() +1);
        category5.addParentCategoryAndDepth(category1, category1.getDepth() +1);

        given(categoryRepository.findByParentIsNull()).willReturn(Optional.of(parent));
        // when
        CategoryDTO.Detail result = categoryService.findAllCategoriesHier();

        // then
        assertThat(result.getName()).isEqualTo(parent.getName());
    }


    @Test
    @DisplayName("카테고리 중복 체크")
    void should_CheckCategoryDuplicate_When_ByNameAndParent() {
        // given
        Category parent = Category.builder()
                .name("부모")
                .parent(null)
                .depth(1L)
                .children(new ArrayList<>())
                .build();

        setField(parent, "id", 1L);

        CategoryDTO.Detail dto = CategoryDTO.Detail.builder()
                .name("카테고리")
                .parent_id(1L)
                .depth(null)
                .build();

        Category entity = dto.toEntity();

        setField(entity, "id", 2L);

        given(categoryRepository.existsByNameAndParent(entity.getName(), entity.getParent())).willReturn(true);

        // when
        boolean check = categoryService.checkCategoryDuplicateByNameAndParent(entity.getName(), entity.getParent());

        // then
        assertThat(check).isTrue();
        then(categoryRepository).should().existsByNameAndParent(entity.getName(), entity.getParent());
    }
}
