package firstskin.firstskin.admin.service;

import firstskin.firstskin.admin.api.dto.request.EditCategory;
import firstskin.firstskin.admin.api.dto.response.CategoryResponse;
import firstskin.firstskin.category.domain.Category;
import firstskin.firstskin.category.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 전체 조회하면 전체 목록이 반환된다")
    @Transactional
    public void getAllCategories() throws Exception{
        //given
        categoryRepository.save(new Category("스킨/로션"));
        categoryRepository.save(new Category("립"));

        //when
        List<CategoryResponse> allCategories = categoryService.getAllCategories();

        //then
        assertThat(allCategories.size()).isEqualTo(2);
        assertThat(allCategories.get(0).getCategory()).isEqualTo("스킨/로션");
        assertThat(allCategories.get(1).getCategory()).isEqualTo("립");
    }

    @Test
    @DisplayName("카테고리를 추가하면 추가된다.")
    @Transactional
    public void addCategory() throws Exception{
        //given
        Category category = new Category("선크림");

        //when
        categoryService.addCategory(category.getCategory());

        //then
        assertThat(categoryRepository.findAll().size()).isEqualTo(1);
        assertThat(categoryRepository.findAll().get(0).getCategory()).isEqualTo("선크림");
    }

    @Test
    @DisplayName("카테고리를 수정하면 이름이 바뀐다")
    @Transactional
    public void editCategory() throws Exception{
        //given
        Category category = new Category("스킨/로션");
        categoryRepository.save(category);

        assertThat(categoryRepository.findById(category.getCategoryId()).get().getCategory()).isEqualTo("스킨/로션");
        //when
        categoryService.editCategory(new EditCategory(category.getCategoryId(), "양념치킨"));

        //then
        assertThat(categoryRepository.findById(category.getCategoryId()).get().getCategory()).isEqualTo("양념치킨");
    }

    @Test
    @DisplayName("카테고리를 삭제하면 삭제된다.")
    public void deleteCategory() throws Exception{
        //given
        Category category = new Category("스킨/로션");
        categoryRepository.save(category);

        assertThat(categoryRepository.findAll().size()).isEqualTo(1);

        //when
        categoryService.deleteCategory(category.getCategoryId());

        //then
        assertThat(categoryRepository.findAll().size()).isEqualTo(0);

    }
}