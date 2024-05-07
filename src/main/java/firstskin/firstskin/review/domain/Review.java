package firstskin.firstskin.review.domain;

import firstskin.firstskin.common.entity.BaseTimeEntity;
import firstskin.firstskin.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Entity
@NoArgsConstructor
@Getter
public class Review extends BaseTimeEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "product_id")
    private Long productId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int score;

    private boolean activated;

    public Review(Member member, Long productId, String content, int score, boolean activated) {
        this.member = member;
        this.productId = productId;
        this.content = content;
        this.score = score;
        this.activated = activated;
    }

    public void update(String content, int score){
        this.content = content;
        this.score = score;
    }





}
