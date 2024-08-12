package firstskin.firstskin.admin.service;

import firstskin.firstskin.admin.api.dto.request.EditCategory;
import firstskin.firstskin.admin.api.dto.response.CategoryResponse;
import firstskin.firstskin.category.domain.Category;
import firstskin.firstskin.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        log.info("모든 카테고리 조회");
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(category.getCategoryId(), category.getCategory())).toList();
    }

    public void addCategory(String category) {
        log.info("카테고리 추가 category: {}", category);
        categoryRepository.save(new Category(category));
    }

    @Transactional
    public void editCategory(EditCategory category) {
        log.info("카테고리 수정 categoryId: {}, category: {}", category.getCategoryId(), category.getCategory());
        Category findCategory = categoryRepository.findById(category.getCategoryId())
                .orElseThrow(IllegalArgumentException::new);

        findCategory.updateCategory(category.getCategory());
    }

    public void deleteCategory(Long categoryId) {
        log.info("카테고리 삭제 categoryId: {}", categoryId);
        categoryRepository.deleteById(categoryId);
    }
}
