package firstskin.firstskin.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.repository.MemberRepository;
import firstskin.firstskin.model.KakaoProfile;
import firstskin.firstskin.model.OauthToken;
import firstskin.firstskin.user.api.dto.MemberDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Autowired
    public MemberService(MemberRepository memberRepository, ObjectMapper objectMapper) {
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
    }


    public Member findMemberByUserId(String userId){
        return memberRepository.findByUserId(userId);
    }
    public Optional<Member> findMemberById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public void addMember(Member member) {
        memberRepository.save(member);
    }

    public List<MemberDto> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    public Optional<MemberDto> getMemberById(Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            return Optional.of(mapToDto(member));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Member> getMemberByIdNotDto(Long memberId){
        return memberRepository.findById(memberId);
    }

    public void updateProfile(Long memberId, MemberDto memberDto) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            memberDto.updateMember(member.getNickname(), member.getProfileUrl());
            memberRepository.save(member);
        } else {
            throw new RuntimeException("Member not found with id: " + memberId);
        }
    }


    public MemberDto mapToDto(Member member) {
        return new MemberDto(
                member.getNickname(),
                member.getProfileUrl(),
                member.getRole()
        );
    }






    public OauthToken requestToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","c33ec31ce21c44a27c43a6165664cb5a");
        params.add("redirect_uri","http://ceprj.gachon.ac.kr:60022/api/oauth/kakao/callback");
        params.add("code",code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        try {
            return objectMapper.readValue(response.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public KakaoProfile requestKakaoProfile(OauthToken oauthToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+oauthToken.getAccess_token());
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        try {
            return objectMapper.readValue(response.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



    public void logoutRequest(String token) {
        String kakaoAccessToken = token;

        String logoutUrl = "https://kapi.kakao.com/v1/user/logout";


        restTemplate.postForObject(logoutUrl + "?access_token=" + kakaoAccessToken, null, String.class);
    }

    public HttpSession sessionSave(HttpServletRequest httpServletRequest, Member member, OauthToken oauthToken) {
        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("memberId", member.getMemberId());
        session.setAttribute("access_token", oauthToken.getAccess_token());
        session.setMaxInactiveInterval(3600);
        log.info("세션 저장 완료. memberId: {}", member.getMemberId());
        log.info("저장된 memberId 세션: {}", session.getAttribute("memberId"));
        return session;
    }


    }

