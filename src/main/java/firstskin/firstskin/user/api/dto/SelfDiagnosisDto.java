package firstskin.firstskin.user.api.dto;

import firstskin.firstskin.skin.Kind;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SelfDiagnosisDto {
    private String Description;
    private String result;
    private String skinPictureUrl;
}
