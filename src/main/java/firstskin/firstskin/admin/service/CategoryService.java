package firstskin.firstskin.admin.service;

import firstskin.firstskin.admin.api.dto.request.EditCategory;
import firstskin.firstskin.admin.api.dto.response.CategoryResponse;
import firstskin.firstskin.category.domain.Category;
import firstskin.firstskin.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(category.getCategoryId(), category.getCategory())).toList();
    }

    public void addCategory(String category) {
        categoryRepository.save(new Category(category));
    }

    @Transactional
    public void editCategory(EditCategory category) {
        Category findCategory = categoryRepository.findById(category.getCategoryId())
                .orElseThrow(IllegalArgumentException::new);

        findCategory.updateCategory(category.getCategory());
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
