package firstskin.firstskin.admin.api.dto.response;

import lombok.Getter;

@Getter
public class CategoryResponse {

    private final Long categoryId;
    private final String category;

    public CategoryResponse(Long categoryId, String category) {
        this.categoryId = categoryId;
        this.category = category;
    }
}
