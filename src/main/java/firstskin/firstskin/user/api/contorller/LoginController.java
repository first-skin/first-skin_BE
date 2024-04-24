package firstskin.firstskin.user.api.contorller;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.model.KakaoProfile;
import firstskin.firstskin.model.OauthToken;
import firstskin.firstskin.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static firstskin.firstskin.member.domain.Role.ROLE_USER;

@Controller
@RequiredArgsConstructor
public class LoginController {

    final private MemberService memberService;

    @GetMapping("/login-kakao")
    public String page(){
        return "login.html";
    }

    @GetMapping("/oauth2/kakao/callback")
    @ResponseBody
    public String login(@RequestParam String code){

        OauthToken oauthToken = memberService.requestToken(code);
        KakaoProfile kakaoProfile = memberService.requestKakaoProfile(oauthToken);

        Member existingMember = memberService.findMemberById(kakaoProfile.getId());

        if (existingMember != null) {

            return "logged_in";
        } else {
            Member newMember = new Member();
            newMember.setRole(ROLE_USER);
            newMember.setProfileUrl(kakaoProfile.getKakao_account().getProfile().getProfile_image_url());
            newMember.setMemberId(kakaoProfile.getId());
            newMember.setNickname(kakaoProfile.getKakao_account().getProfile().getNickname());


            return "new";
        }

    }


}


