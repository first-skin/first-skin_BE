package firstskin.firstskin.dianosis.api.request;

import firstskin.firstskin.skin.Kind;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class DiagnosisDto {

    private Long memberId;
    private Kind kind;
    private MultipartFile file;

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public DiagnosisDto(Long memberId, Kind kind, MultipartFile file) {
        this.memberId = memberId;
        this.kind = kind;
        this.file = file;
    }
}
