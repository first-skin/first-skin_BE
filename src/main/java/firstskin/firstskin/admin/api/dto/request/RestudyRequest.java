package firstskin.firstskin.admin.api.dto.request;

import firstskin.firstskin.skin.Kind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RestudyRequest {

    @Schema(description = "재학습 종류", example = "TYPE or PERSONAL_COLOR or TROUBLE or 타입")
    private final Kind kind;

    @Schema(description = "모델 경로", example = "/home/firstskin/model/firstskin_model.h5")
    private final String modelPath;

    @Schema(description = "CSV 파일 경로", example = "/home/firstskin/csv/firstskin.csv")
    private final String dfPath;

    @Builder
    public RestudyRequest(Kind kind, String modelPath, String csvPath) {
        this.kind = kind;
        this.modelPath = modelPath;
        this.dfPath = csvPath;
    }
}
