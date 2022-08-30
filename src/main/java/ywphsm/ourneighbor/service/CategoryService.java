package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Category;
import ywphsm.ourneighbor.domain.dto.CategoryDTO;
import ywphsm.ourneighbor.repository.category.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 등록
    @Transactional
    public Long saveCategory(CategoryDTO dto) {
        
        // parent, child는 빠져있음
        Category category = dto.toEntity();

        log.info("dto.getParent_id={}", dto.getParentId());

        // 가장 상위 (depth : 1L) 카테고리 생성
        if (dto.getParentId() == null) {

            // 모든 카테고리를 다 볼 수 있는 루트 카테고리
            // 없으면 생성함
            Category root = categoryRepository.findByNameAndDepth("ROOT", 0L)
                    .orElseGet(() -> Category.builder()
                            .name("ROOT")
                            .depth(0L)
                            .build()
            );
            
            // 이전에 만들어진 적이 없는 경우에만 루트 카테고리를 저장
            if (!categoryRepository.existsByNameAndDepth("ROOT", 0L)) {
                categoryRepository.save(root);
            }

            category.addParentCategoryAndDepth(root, 1L);
        } else {
            Category parent = categoryRepository.findById(dto.getParentId()).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 상위 카테고리입니다."));

            category.addParentCategoryAndDepth(parent, parent.getDepth()+1);
            parent.getChildren().add(category);
        }

        return categoryRepository.save(category).getId();
    }

    // 단순히 모든 카테고리들을 보여주는 쿼리
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    // 하나의 쿼리로 모든 하위 카테고리를 연쇄적으로 뽑아내기 위한 쿼리
    public List<CategoryDTO> findAllCategoriesHier() {
        return categoryRepository.findCategories().stream().map(CategoryDTO::of).collect(Collectors.toList());
    }
}
