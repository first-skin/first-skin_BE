package firstskin.firstskin.dianosis.service;

import firstskin.firstskin.dianosis.api.request.DiagnosisDto;
import firstskin.firstskin.dianosis.api.response.DiagnosisResponse;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DiagnosisServiceTest {

    @Autowired
    private DiagnosisService diagnosisService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    SkinRepository skinRepository;

    @Test
    @DisplayName("피부 타입 진단 테스트를 실행하면 진단 결과가 반환된다")
    @Transactional
//    @Rollback(value = false)
    public void diagnosisType() throws Exception{
        //given
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();

        Member member1 = new Member(Role.ROLE_USER, "prf1", uuid1, "member1Nick");
        Member member2 = new Member(Role.ROLE_USER, "prf2", uuid2, "member2Nick");

        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        Path drypath = Paths.get("src/main/resources/test/dry.png");
        Path oilypath = Paths.get("src/main/resources/test/oily.png");
        byte[] dryContent = Files.readAllBytes(drypath);
        byte[] oilyContent = Files.readAllBytes(oilypath);
        MockMultipartFile img = new MockMultipartFile("file", drypath.getFileName().toString(), "image/png", dryContent);
        MockMultipartFile img2 = new MockMultipartFile("file", oilypath.getFileName().toString(), "image/png", oilyContent);

        DiagnosisDto dryRequest = new DiagnosisDto(savedMember1.getMemberId(), Kind.TYPE, img);
        DiagnosisDto oilyRequest = new DiagnosisDto(savedMember2.getMemberId(), Kind.TYPE, img2);

        Skin dry = new Skin(Kind.TYPE, "dry");
        Skin oily = new Skin(Kind.TYPE, "oily");
        Skin normal = new Skin(Kind.TYPE, "normal");
        if (skinRepository.findByResult("dry").isEmpty()) {
            skinRepository.save(dry);
        }
        if (skinRepository.findByResult("oily").isEmpty()) {
            skinRepository.save(oily);
        }
        if (skinRepository.findByResult("normal").isEmpty()) {
            skinRepository.save(normal);
        }

        //when
        DiagnosisResponse response = diagnosisService.diagnosisSkin(dryRequest);

        DiagnosisResponse response2 = diagnosisService.diagnosisSkin(oilyRequest);

        //then
        assertThat(response.getResult()).isEqualTo("dry");
        assertThat(response2.getResult()).isEqualTo("oily");
        assertThat(response.getResult()).isNotEqualTo(response2.getResult());
        assertThat(response.getResult()).isNotEqualTo("normal");

    }

    @Test
    @DisplayName("피부 트러블 진단 테스트를 실행하면 진단 결과가 반환된다")
    @Transactional
//    @Rollback(value = false)
    public void diagnosisTrouble() throws Exception{
        //given
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();

        Member member1 = new Member(Role.ROLE_USER, "prf1", uuid1, "member1Nick");
        Member member2 = new Member(Role.ROLE_USER, "prf2", uuid2, "member2Nick");

        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        Path drypath = Paths.get("src/main/resources/test/dry.png");
        Path oilypath = Paths.get("src/main/resources/test/trouble.png");
        byte[] dryContent = Files.readAllBytes(drypath);
        byte[] oilyContent = Files.readAllBytes(oilypath);
        MockMultipartFile img = new MockMultipartFile("file", drypath.getFileName().toString(), "image/png", dryContent);
        MockMultipartFile img2 = new MockMultipartFile("file", oilypath.getFileName().toString(), "image/png", oilyContent);

        DiagnosisDto normalRequest = new DiagnosisDto(savedMember1.getMemberId(), Kind.TROUBLE, img);
        DiagnosisDto troubleRequest = new DiagnosisDto(savedMember2.getMemberId(), Kind.TROUBLE, img2);

        Skin normal = new Skin(Kind.TROUBLE, "normal");
        Skin trouble = new Skin(Kind.TROUBLE, "trouble");
        Skin acne = new Skin(Kind.TROUBLE, "acne");
        Skin redness = new Skin(Kind.TROUBLE, "redness");
        if (skinRepository.findByResult("normal").isEmpty()) {
            skinRepository.save(normal);
        }
        if (skinRepository.findByResult("trouble").isEmpty()) {
            skinRepository.save(trouble);
        }
        if (skinRepository.findByResult("redness").isEmpty()) {
            skinRepository.save(redness);
        }
        if (skinRepository.findByResult("acne").isEmpty()) {
            skinRepository.save(acne);
        }

        //when
        DiagnosisResponse response = diagnosisService.diagnosisSkin(normalRequest);

        DiagnosisResponse response2 = diagnosisService.diagnosisSkin(troubleRequest);

        //then
        assertThat(response.getResult()).isEqualTo("normal");
        assertThat(response2.getResult()).isEqualTo("redness");
        assertThat(response.getResult()).isNotEqualTo(response2.getResult());
        assertThat(response.getResult()).isNotEqualTo("acne");

    }
}