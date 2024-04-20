package firstskin.firstskin.category.repository;

import firstskin.firstskin.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>{

}
