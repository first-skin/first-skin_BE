package firstskin.firstskin.dianosis.domain;

import firstskin.firstskin.common.entity.BaseTimeEntity;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.skin.Skin;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Diagnosis extends BaseTimeEntity {

    @Id
    @Column(name = "diagnosis_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diagnosisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skin_id")
    private Skin skin;

    @Column(name = "skin_picture_url")
    private String skinPictureUrl;

    @Builder
    public Diagnosis(Member member, Skin skin, String skinPictureUrl) {
        this.member = member;
        this.skin = skin;
        this.skinPictureUrl = skinPictureUrl;
    }
}
