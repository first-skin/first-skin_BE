package firstskin.firstskin.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long categoryId;

    private String category;
}
