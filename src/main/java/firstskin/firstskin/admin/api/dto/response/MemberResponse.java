package firstskin.firstskin.admin.api.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponse {

    private Long memberId;
    private String userId;
    private String profile;
    private LocalDateTime createdDate;
    private String nickname;
    private String type;
    private String trouble;
    private String personalColor;

    public MemberResponse(Long memberId, String userId, String profile, LocalDateTime createdDate, String nickname, String type, String trouble, String personalColor) {
        this.memberId = memberId;
        this.userId = userId;
        this.profile = profile;
        this.createdDate = createdDate;
        this.nickname = nickname;
        this.type = type;
        this.trouble = trouble;
        this.personalColor = personalColor;
    }
}
