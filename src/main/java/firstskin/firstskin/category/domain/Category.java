package firstskin.firstskin.category.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long categoryId;

    private String category;

    public Category(String category) {
        this.category = category;
    }

    public void updateCategory(String category) {
        this.category = category;
    }
}