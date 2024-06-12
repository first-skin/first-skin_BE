package firstskin.firstskin.dianosis.api.request;

import firstskin.firstskin.skin.Kind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisDto {

    private Long memberId;
    private Kind kind;
    private MultipartFile file;
    private String sex;

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public DiagnosisDto(Long memberId, Kind kind, MultipartFile file, String sex) {
        this.memberId = memberId;
        this.kind = kind;
        this.file = file;
        this.sex = sex;
    }
}
