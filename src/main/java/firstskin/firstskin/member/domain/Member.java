package firstskin.firstskin.member.domain;

import firstskin.firstskin.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(name = "user_id")
    private String userId;

    private String name;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "profile_url")
    private String profileUrl;

    private boolean activated;


    public Member(Role role, String profileImageUrl, String userId, String nickname) {
        this.role = role;
        this.profileUrl = profileImageUrl;
        this.userId = userId;
        this.nickname = nickname;
    }

    public void delete() {
        this.activated = false;
    }

    @PrePersist
    public void prePersist() {
        this.activated = true;
    }
}
