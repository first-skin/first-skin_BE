package firstskin.firstskin.skin;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Kind {

    TYPE("피부 타입"),
    TROUBLE("트러블"),
    PERSONAL_COLOR("퍼스널컬러");

    private final String description;
}
