package firstskin.firstskin.user.service;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.repository.MemberRepository;
import firstskin.firstskin.review.domain.Review;
import firstskin.firstskin.review.repository.ReviewRepository;
import firstskin.firstskin.user.api.dto.UpdateReview;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getAllReviews(Long productId){
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> getAllMemberReviews(Long memberId) {
        return reviewRepository.findByMember_MemberId(memberId);
    }

    public void addReview(Member member, Long productId, String content, int score){
        Review newReview = new Review(member, productId, content, score, true);
        reviewRepository.save(newReview);
    }

    public void updateReview(UpdateReview review){
        Review findReview = reviewRepository.findById(review.getReviewId())
                .orElseThrow(IllegalArgumentException::new);

        findReview.update(review.getContent(),review.getScore());

    }

    public void deleteReview(Long reviewId){
        reviewRepository.deleteById(reviewId);

    }

    public List<Review> getAllReviewsSortedByScore(Long productId) {
        return reviewRepository.findByProductId(productId, Sort.by(Sort.Direction.DESC, "score"));
    }
}
