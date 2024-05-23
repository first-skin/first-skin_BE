package firstskin.firstskin.user.api.contorller;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.model.KakaoProfile;
import firstskin.firstskin.model.OauthToken;
import firstskin.firstskin.user.api.dto.MemberDto;
import firstskin.firstskin.user.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static firstskin.firstskin.member.domain.Role.ROLE_USER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberController {

    final private MemberService memberService;

    @GetMapping("/oauth/kakao/callback")
    public ResponseEntity<String> login(@RequestParam String code, HttpServletRequest httpServletRequest) {

        log.info("로그인 요청 받음. code: {}", code);

        OauthToken oauthToken = memberService.requestToken(code);
        KakaoProfile kakaoProfile = memberService.requestKakaoProfile(oauthToken);

        Member findMember = memberService.findMemberByUserId(kakaoProfile.getId().toString());
        if (findMember != null) {

            log.info("==기존 회원 로그인==");
            memberService.sessionSave(httpServletRequest, findMember, oauthToken);
            log.info("기존 회원 로그인. userId: {}", findMember.getUserId());
            return ResponseEntity.status(200).body("login");
        } else {
            log.info("==신규 회원 가입==");
            Member newMember = new Member(
                    ROLE_USER,
                    kakaoProfile.getKakao_account().getProfile().getProfile_image_url(),
                    kakaoProfile.getId().toString(),
                    kakaoProfile.getKakao_account().getProfile().getNickname());

            memberService.addMember(newMember);
            memberService.sessionSave(httpServletRequest, newMember, oauthToken);

            log.info("신규 회원 가입. userId: {}", newMember.getUserId());
            return ResponseEntity.status(200).body("new-member");
        }
    }

    @GetMapping("/logout-kakao")
    public ResponseEntity<String> logoutKakao(HttpServletRequest request) {
        String accessToken = (String) request.getSession().getAttribute("access_token");

        memberService.logoutRequest(accessToken);

        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("로그아웃 memberId: {}", session.getAttribute("memberId"));
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
    public ResponseEntity<Optional<MemberDto>> getMember(@PathVariable Long memberId) {
        Optional<MemberDto> member = memberService.getMemberById(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(member);
    }

    @PutMapping("/members/{memberId}")
    public ResponseEntity<String> updateProfile(@PathVariable Long memberId, @RequestBody MemberDto memberDto) {
        memberService.updateProfile(memberId, memberDto);
        return ResponseEntity.ok("Profile updated successfully");

    }

    @GetMapping("/members/is-login")
    public ResponseEntity<String> isLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        Object memberId = session.getAttribute("memberId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("로그인 되어있습니다. memberId: " + memberId);
    }
}


