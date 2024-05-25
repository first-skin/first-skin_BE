package firstskin.firstskin.user.service;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.review.domain.Review;
import firstskin.firstskin.review.domain.ReviewImage;
import firstskin.firstskin.review.repository.ReviewImageRepository;
import firstskin.firstskin.review.repository.ReviewRepository;
import firstskin.firstskin.user.api.dto.UpdateReview;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    public ReviewService(ReviewRepository reviewRepository, ReviewImageRepository reviewImageRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
    }

    public List<Review> getAllProductReviews(Long productId){
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> getAllMemberReviews(Long memberId) {
        return reviewRepository.findByMember_MemberId(memberId);
    }

    @Transactional
    public void addReview(Member member, Long productId, String content, int score, List<String> reviewImages) {
        // 리뷰 생성
        Review newReview = new Review(member, productId, content, score, true);

        // 리뷰 이미지 생성 및 추가
        for (String imageUrl : reviewImages) {
            ReviewImage reviewImage = new ReviewImage(newReview, imageUrl);
            reviewImageRepository.save(reviewImage);
        }
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
