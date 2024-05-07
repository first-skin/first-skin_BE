package firstskin.firstskin.user.api.contorller;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.model.KakaoProfile;
import firstskin.firstskin.model.OauthToken;
import firstskin.firstskin.user.api.dto.MemberDto;
import firstskin.firstskin.user.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static firstskin.firstskin.member.domain.Role.ROLE_USER;

@RestController
@RequiredArgsConstructor
public class MemberController {

    final private MemberService memberService;


    @GetMapping("/oauth2/kakao/callback")
    public ResponseEntity<String> login(@RequestParam String code, HttpServletRequest httpServletRequest){

        OauthToken oauthToken = memberService.requestToken(code);
        KakaoProfile kakaoProfile = memberService.requestKakaoProfile(oauthToken);

        Member existingMember = memberService.findMemberById(kakaoProfile.getId())
                .orElseThrow(IllegalArgumentException::new);

        if (existingMember != null) {
            memberService.sessionSave(httpServletRequest, existingMember, oauthToken);
            return ResponseEntity.status(HttpStatus.OK).body("Logged-in");
        } else {
            Member newMember = new Member(
                    ROLE_USER,
                    kakaoProfile.getKakao_account().getProfile().getProfile_image_url(),
                    kakaoProfile.getId().toString(),
                    kakaoProfile.getKakao_account().getProfile().getNickname());

            memberService.addMember(newMember);
            memberService.sessionSave(httpServletRequest, newMember, oauthToken);

            return ResponseEntity.status(HttpStatus.OK).body("new-member");
        }
    }
    @GetMapping("/logout-kakao")
    public ResponseEntity<String> logoutKakao(HttpServletRequest request) {
        String accessToken = (String) request.getSession().getAttribute("access_token");

        memberService.logoutRequest(accessToken);

        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }

        return ResponseEntity.status(HttpStatus.OK).body("Logged-out-successfully");

    }
    @GetMapping("/members")
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        List<MemberDto> members = memberService.getAllMembers();

        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<Optional<MemberDto>> getMember(@PathVariable Long memberId){
        Optional<MemberDto> member = memberService.getMemberById(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(member);
    }

    @PutMapping("/members/{memberId}")
    public ResponseEntity<String> updateProfile(@PathVariable Long memberId, @RequestBody MemberDto memberDto) {
            memberService.updateProfile(memberId, memberDto);
            return ResponseEntity.ok("Profile updated successfully");

    }


}


