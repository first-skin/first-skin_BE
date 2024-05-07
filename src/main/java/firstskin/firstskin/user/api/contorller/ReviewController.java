package firstskin.firstskin.user.api.contorller;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.review.domain.Review;
import firstskin.firstskin.user.api.dto.UpdateReview;
import firstskin.firstskin.user.service.MemberService;
import firstskin.firstskin.user.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;


    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{productId}")
    public List<Review> getAllReviews(@PathVariable Long productId) {
        return reviewService.getAllReviews(productId);
    }

    @GetMapping("/sorted/{productId}")
    public List<Review> getAllReviewsSortedByScore(@PathVariable Long productId) {
        return reviewService.getAllReviewsSortedByScore(productId);
    }
    @GetMapping("/members/{memberId}/reviews")
    public List<Review> getAllMemberReviews(@PathVariable Long memberId) {
        return getAllReviews(memberId);
    }

    @PostMapping
    public void addReview(@RequestBody Review request) {
        reviewService.addReview(request.getMember(), request.getProductId(), request.getContent(), request.getScore());
    }

    @PutMapping
    public void updateReview(@RequestBody UpdateReview request) {
            reviewService.updateReview(request);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }


}