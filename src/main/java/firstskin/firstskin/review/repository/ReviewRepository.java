package firstskin.firstskin.review.repository;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.review.domain.Review;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId, Sort score);
    List<Review> findByProductId(Long productId);

    List<Review> findByMember_MemberId(Long memberId);
}
