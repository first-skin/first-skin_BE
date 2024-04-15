package firstskin.firstskin.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ROLE_ADMIN("관리자"),
    ROLE_USER("사용자");

    private final String role;
}
