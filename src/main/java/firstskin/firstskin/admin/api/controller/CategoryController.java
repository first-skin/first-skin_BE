package firstskin.firstskin.admin.api.controller;

import firstskin.firstskin.admin.api.dto.request.EditCategory;
import firstskin.firstskin.admin.api.dto.response.CategoryResponse;
import firstskin.firstskin.admin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category")
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/category")
    public void addCategory(String category) {
        categoryService.addCategory(category);
    }

    @PutMapping("/category")
    public void updateCategory(EditCategory category) {
        categoryService.editCategory(category);
    }

    @DeleteMapping("/category/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
