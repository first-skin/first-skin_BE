package firstskin.firstskin.user.api.dto;

import lombok.Getter;

@Getter
public class UpdateReview {
    private final String content;

    private final int score;

    private final Long reviewId;


    public UpdateReview(String content, int score, Long reviewId) {
        this.content = content;
        this.score = score;
        this.reviewId = reviewId;
    }
}
