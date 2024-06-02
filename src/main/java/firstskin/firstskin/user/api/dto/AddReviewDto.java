package firstskin.firstskin.user.api.dto;

import firstskin.firstskin.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.util.List;

@Getter
public class AddReviewDto {

    private Long memberId;


    private Long productId;


    private String content;

    private int score;


    private List<String> reviewImages;

    public AddReviewDto(Long memberId, Long productId, String content, int score, List<String> reviewImages) {
        this.memberId = memberId;
        this.productId = productId;
        this.content = content;
        this.score = score;
        this.reviewImages = reviewImages;

    }
}
