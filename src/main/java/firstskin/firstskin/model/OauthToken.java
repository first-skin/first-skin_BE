package firstskin.firstskin.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OauthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String id_token;
    private Integer expires_in;
    private String scope;
    private Integer refresh_token_expires_in;
}
