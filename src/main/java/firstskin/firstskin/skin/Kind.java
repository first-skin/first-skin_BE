package firstskin.firstskin.skin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Kind {

    TYPE("피부타입"),
    TROUBLE("트러블"),
    PERSONAL_COLOR("퍼스널컬러");

    private final String description;
}
