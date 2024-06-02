package firstskin.firstskin.dianosis.api.response;

import lombok.Getter;

@Getter
public class DiagnosisResponse {

    private String result;

    public DiagnosisResponse(String result) {
        this.result = result;
    }
}
