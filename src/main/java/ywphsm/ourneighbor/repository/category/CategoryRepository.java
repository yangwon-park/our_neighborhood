package ywphsm.ourneighbor.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.category.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    Category findByName(String name);

    Optional<Category> findByNameAndDepth(String name, Long depth);

    Optional<Category> findByParentIsNull();

    List<Category> findAllByOrderByDepthAscParentIdAscNameAsc();

    Boolean existsByNameAndDepth(String name, Long depth);

    Boolean existsByNameAndParent(String name, Category parent);
}
