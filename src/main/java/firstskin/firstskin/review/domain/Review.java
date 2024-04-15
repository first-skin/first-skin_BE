package firstskin.firstskin.review.domain;

import firstskin.firstskin.common.entity.BaseTimeEntity;
import firstskin.firstskin.member.domain.Member;
import jakarta.persistence.*;

@Entity
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
}
