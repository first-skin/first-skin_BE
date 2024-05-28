package firstskin.firstskin.admin.api.controller;

import firstskin.firstskin.category.domain.Category;
import firstskin.firstskin.category.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("카테고리 전체 조회 테스트")
    @Transactional
    public void getAllCategories() throws Exception {
        //given
        categoryRepository.save(new Category("스킨/로션"));
        categoryRepository.save(new Category("립"));

        //expected
        mockMvc.perform(get("/api/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("카테고리 추가 테스트")
    @Transactional
    public void addCategory() throws Exception{
        //given
        String category = "선크림";

        //expected
        mockMvc.perform(get("/api/category")
                        .param("category", category)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    @Transactional
    public void editCategory() throws Exception{
        //given
        Category category = new Category("스킨/로션");
        categoryRepository.save(category);

        //expected
        mockMvc.perform(get("/api/category")
                        .param("categoryId", String.valueOf(category.getCategoryId()))
                        .param("category", "선크림")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    @Transactional
    public void deleteCategory() throws Exception{
        //given
        Category category = new Category("스킨/로션");
        categoryRepository.save(category);
        mockMvc.perform(get("/api/category")
                        .param("categoryId", String.valueOf(category.getCategoryId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        //expected
        mockMvc.perform(delete("/api/admin/category/{categoryId}", category.getCategoryId())
                        .param("categoryId", String.valueOf(category.getCategoryId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }
}