package firstskin.firstskin.admin.api.dto.request;

import lombok.Getter;

@Getter
public class EditCategory {

    private final Long categoryId;
    private final String category;

    public EditCategory(Long categoryId, String category) {
        this.categoryId = categoryId;
        this.category = category;
    }
}
