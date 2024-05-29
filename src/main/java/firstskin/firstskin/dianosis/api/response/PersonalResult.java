package firstskin.firstskin.dianosis.api.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PersonalResult {

    private String type;

    private String personalColor;

    private String trouble;

    public PersonalResult(String type, String personalColor, String trouble) {
        this.type = type;
        this.personalColor = personalColor;
        this.trouble = trouble;
    }
}
