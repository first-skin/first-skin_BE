package firstskin.firstskin.review.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ReviewImage {

    @Id
    @GeneratedValue
    @Column(name = "review_image_id")
    private Long reviewImageId;

    @JoinColumn(name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Review review;

    @Column(name = "review_image_url")
    private String reviewImageUrl;

    public ReviewImage(Review review, String reviewImageUrl) {
        this.review = review;
        this.reviewImageUrl = reviewImageUrl;
    }

    public ReviewImage() {

    }
}
