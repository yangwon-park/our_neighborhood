package ywphsm.ourneighbor.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ywphsm.ourneighbor.domain.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    @Query("select c from Category c where c.parent is NULL")
    List<Category> findCategories();
}
