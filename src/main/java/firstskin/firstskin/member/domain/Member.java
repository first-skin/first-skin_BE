package firstskin.firstskin.member.domain;

import firstskin.firstskin.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue
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



}
