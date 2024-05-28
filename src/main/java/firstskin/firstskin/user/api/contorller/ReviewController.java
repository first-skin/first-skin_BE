package firstskin.firstskin.user.api.contorller;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.review.domain.Review;
import firstskin.firstskin.user.api.dto.UpdateReview;
import firstskin.firstskin.user.service.MemberService;
import firstskin.firstskin.user.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<String> addReview(HttpSession session,
                                            @RequestParam("productId") Long productId,
                                            @RequestParam("content") String content,
                                            @RequestParam("score") int score,
                                            @RequestPart("reviewImages") List<MultipartFile> reviewImages) throws IOException {
        Long memberId = (Long) session.getAttribute("memberId");
        Optional<Member> member = memberService.getMemberByIdNotDto(memberId);
        reviewService.addReview(member.get(), productId, content, score, reviewImages);
        return ResponseEntity.ok("Review added successfully.");
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