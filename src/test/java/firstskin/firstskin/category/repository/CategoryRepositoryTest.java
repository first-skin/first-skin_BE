package firstskin.firstskin.category.repository;

import firstskin.firstskin.category.domain.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 전체 조회 테스트")
    @Transactional
    public void findAllCategory() throws Exception{
        //given
        Category category1 = new Category("스킨/로션");
        Category category2 = new Category("립");
        Category category3 = new Category("아이");

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        //when
        List<Category> result = categoryRepository.findAll();

        //then
//        assertThat(result.size()).isEqualTo(3);
//        assertThat(result).extracting("category").containsExactly("스킨/로션", "립", "아이");

    }
}