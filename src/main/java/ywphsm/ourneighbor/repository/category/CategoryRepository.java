package ywphsm.ourneighbor.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ywphsm.ourneighbor.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {
}
