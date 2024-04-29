package firstskin.firstskin.user.api.dto;

import firstskin.firstskin.member.domain.Role;
import lombok.*;

@Getter
public class MemberDto {


    private String nickname;
    private Role role;
    private String profileUrl;

    public MemberDto(String nickname, String profileUrl, Role role) {
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.role = role;
    }

    public void updateMember(String nickname, String profileUrl) {
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }




}
