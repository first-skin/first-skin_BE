package firstskin.firstskin.review.domain;

import jakarta.persistence.*;

@Entity
public class ReviewImage {

    @Id
    @GeneratedValue
    @Column(name = "review_image_id")
    private Long reviewImageId;

    @Column(name = "review_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Column(name = "review_image_url")
    private String reviewImageUrl;
}
