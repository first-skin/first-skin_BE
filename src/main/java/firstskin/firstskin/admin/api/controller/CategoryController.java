package firstskin.firstskin.admin.api.controller;

import firstskin.firstskin.admin.api.dto.request.EditCategory;
import firstskin.firstskin.admin.api.dto.response.CategoryResponse;
import firstskin.firstskin.admin.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category")
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/admin/category")
    public void addCategory(String category, HttpServletRequest request) {
        categoryService.addCategory(category);
    }

    @PutMapping("/admin/category")
    public void updateCategory(EditCategory category, HttpServletRequest request) {
        categoryService.editCategory(category);
    }

    @DeleteMapping("/admin/category/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId, HttpServletRequest request) {
        categoryService.deleteCategory(categoryId);
    }
}
