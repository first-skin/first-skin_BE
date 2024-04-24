package firstskin.firstskin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown=true)
@Data
public class KakaoProfile {

    public Long id;
    //public String connectedAt;
    //public Properties properties;
    public KakaoAccount kakao_account;

    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Properties {
        //public String profileImage;
        //public String thumbnailImage;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class KakaoAccount {

        //public Boolean profileNicknameNeedsAgreement;
        //public Boolean profileImageNeedsAgreement;
        public Profile profile;
        //public Boolean hasEmail;
        //public Boolean emailNeedsAgreement;
        //public Boolean isEmailValid;
        // Boolean isEmailVerified;
        //public String email;
        public String name;

        @Data
        @JsonIgnoreProperties(ignoreUnknown=true)
        public static class Profile {

            public String nickname;
            //public String thumbnailImageUrl;
           public String profile_image_url;
            // Boolean isDefaultImage;
           // public Boolean isDefaultNickname;

        }
    }


}


