package firstskin.firstskin.admin.api.dto.response;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class MemberResponse {

    private final Long memberId;
    private final String userId;
    private final String profile;
    private final LocalDateTime createdDate;
    private final String nickname;
    private final String type;
    private final String trouble;
    private final String personalColor;

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
