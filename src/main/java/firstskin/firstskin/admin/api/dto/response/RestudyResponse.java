package firstskin.firstskin.admin.api.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestudyResponse {

    private Double before;

    private Double after;

    private String result;

    @Builder
    public RestudyResponse(Double before, Double after, String result) {
        this.before = before;
        this.after = after;
        this.result = result;
    }
}
