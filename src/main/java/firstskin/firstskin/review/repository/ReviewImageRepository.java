package firstskin.firstskin.review.repository;

import firstskin.firstskin.review.domain.Review;
import firstskin.firstskin.review.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReview(Review review);
}
