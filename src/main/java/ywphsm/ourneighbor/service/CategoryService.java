package ywphsm.ourneighbor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ywphsm.ourneighbor.domain.Category;
import ywphsm.ourneighbor.domain.dto.CategoryAddDTO;
import ywphsm.ourneighbor.domain.dto.CategoryDTO;
import ywphsm.ourneighbor.repository.category.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 등록
    @Transactional
    public Long saveCategory(CategoryAddDTO dto) {
        return categoryRepository.save(dto.toEntity()).getId();
    }

    public List<CategoryDTO> findAllCategories() {
        return categoryRepository.findCategories().stream().map(CategoryDTO::of).collect(Collectors.toList());
    }
}
