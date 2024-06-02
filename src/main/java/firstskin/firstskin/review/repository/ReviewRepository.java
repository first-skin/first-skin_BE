package firstskin.firstskin.review.repository;

import firstskin.firstskin.review.domain.Review;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId, Sort score);
    List<Review> findByProductId(Long productId);

    List<Review> findByMember_MemberId(Long memberId);

    @Query("SELECT AVG(r.score) FROM Review r WHERE r.productId = :productId")
    Double findAvgScoreByProductId(Long productId);
}
