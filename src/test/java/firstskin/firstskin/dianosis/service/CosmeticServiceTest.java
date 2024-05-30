package firstskin.firstskin.dianosis.service;

import firstskin.firstskin.dianosis.DiagnosisRepository;
import firstskin.firstskin.dianosis.api.request.CosmeticPersonal;
import firstskin.firstskin.dianosis.api.response.CosmeticPageResponse;
import firstskin.firstskin.dianosis.domain.Diagnosis;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.domain.Role;
import firstskin.firstskin.member.repository.MemberRepository;
import firstskin.firstskin.skin.Kind;
import firstskin.firstskin.skin.Skin;
import firstskin.firstskin.skin.repository.SkinRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CosmeticServiceTest {

    @Autowired
    DiagnosisRepository diagnosisRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    SkinRepository skinRepository;

    @Autowired
    CosmeticService cosmeticService;

    @Test
    @DisplayName("화장품 검색 테스트 개인별로 하면 결과 나옴")
    public void searchPersonalCosmetics() throws Exception{
        //given
        Member member = new Member(Role.ROLE_USER, "프ㅡㅡ로필", "userisd", "niasdhn");
        Skin skin = new Skin(Kind.TYPE, "지성");

        Skin savedSkin = skinRepository.save(skin);
        Member savedMember = memberRepository.save(member);

        Diagnosis build = Diagnosis.builder()
                .member(savedMember)
                .skin(savedSkin)
                .skinPictureUrl("https://naver.com")
                .build();
        Diagnosis saveDiagnosis = diagnosisRepository.save(build);

        CosmeticPersonal cosmeticPersonal = new CosmeticPersonal("로션", 1, 10, "sim");

        //when
         CosmeticPageResponse cosmeticPageResponse = cosmeticService.searchPersonalCosmetics(savedMember.getMemberId(), cosmeticPersonal);

        //then

    }

}