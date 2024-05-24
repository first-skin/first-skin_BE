package firstskin.firstskin.user.api.contorller;

import firstskin.firstskin.review.domain.Review;
import firstskin.firstskin.user.api.dto.AddReviewDto;
import firstskin.firstskin.user.api.dto.MemberDto;
import firstskin.firstskin.user.api.dto.UpdateReview;
import firstskin.firstskin.user.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final MemberService memberService;


    public ReviewController(ReviewService reviewService, MemberService memberService) {
        this.reviewService = reviewService;
        this.memberService = memberService;
    }

    @GetMapping("/{productId}")
    public List<Review> getAllReviews(@PathVariable Long productId) {
        return reviewService.getAllProductReviews(productId);
    }

    @GetMapping("/sorted/{productId}")
    public List<Review> getAllReviewsSortedByScore(@PathVariable Long productId) {
        return reviewService.getAllReviewsSortedByScore(productId);
    }
    @GetMapping("/members/{memberId}/reviews")
    public List<Review> getAllMemberReviews(@PathVariable Long memberId) {
        return reviewService.getAllMemberReviews(memberId);
    }

    @PostMapping
    public void addReview(@RequestBody AddReviewDto request) {
        Long memberId = request.getMemberId();
        Optional<Member> member = memberService.getMemberByIdNotDto(memberId);

        reviewService.addReview(member.get(), request.getProductId(), request.getContent(), request.getScore(), request.getReviewImages() );
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