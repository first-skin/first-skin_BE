package firstskin.firstskin.dianosis.service;

import firstskin.firstskin.dianosis.DiagnosisRepository;
import firstskin.firstskin.dianosis.api.request.DiagnosisDto;
import firstskin.firstskin.dianosis.api.response.DiagnosisResponse;
import firstskin.firstskin.dianosis.domain.Diagnosis;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.domain.Role;
import firstskin.firstskin.member.repository.MemberRepository;
import firstskin.firstskin.skin.Kind;
import firstskin.firstskin.skin.Skin;
import firstskin.firstskin.skin.repository.SkinRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static firstskin.firstskin.member.domain.Role.ROLE_USER;

@SpringBootTest
class DiagnosisServiceTest {

    @Autowired
    DiagnosisService diagnosisService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DiagnosisRepository diagnosisRepository;

    @Autowired
    SkinRepository skinRepository;

    @Test
    @DisplayName("퍼스널 컬러진단을 수행하면 결과가 반환된다.")
    @Transactional
    public void diagnosisPersonalColor() throws Exception{
        //given
        skinRepository.save(new Skin(Kind.PERSONAL_COLOR, "spring"));
        skinRepository.save(new Skin(Kind.PERSONAL_COLOR, "summer"));
        skinRepository.save(new Skin(Kind.PERSONAL_COLOR, "fall"));
        skinRepository.save(new Skin(Kind.PERSONAL_COLOR, "winter"));
        Member member = new Member(Role.ROLE_USER, "프ㅡㅡ로필", "userisd", "niasdhn");
        Member savedMember = memberRepository.save(member);

        MockMultipartFile pngMockMultipartFile = getPngMockMultipartFile("src/main/resources/test/personal_color.png");

        DiagnosisDto diagnosisDto = new DiagnosisDto(savedMember.getMemberId(), Kind.PERSONAL_COLOR, pngMockMultipartFile, "male");
        //when
        DiagnosisResponse response = diagnosisService.diagnosisSkin(diagnosisDto);

        List<Diagnosis> all = diagnosisRepository.findAll();

        for (Diagnosis diagnosis : all) {
            System.out.println("diagnosis = " + diagnosis.getSkin().getResult());
        }



    }

    private MockMultipartFile getPngMockMultipartFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream input = new FileInputStream(file);
        return new MockMultipartFile("file", file.getName(), "image/png", input);
    }

    @Test
    @DisplayName("트러블 진단을 수행하면 결과가 반환된다.")
    @Transactional
    public void diagnosisTrouble() throws Exception{
        //given
        skinRepository.save(new Skin(Kind.TROUBLE, "acne"));
        skinRepository.save(new Skin(Kind.TROUBLE, "troublenormal"));
        skinRepository.save(new Skin(Kind.TROUBLE, "redness"));

        Member member = new Member(ROLE_USER, "profile", "user", "nick");
        Member savedMember = memberRepository.save(member);

        MockMultipartFile pngMockMultipartFile = getPngMockMultipartFile("src/main/resources/test/tttt.jpeg");
//        MockMultipartFile pngMockMultipartFile = getPngMockMultipartFile("src/main/resources/test/dry.png");
//        MockMultipartFile pngMockMultipartFile = getPngMockMultipartFile("src/main/resources/test/personal_color.png");
//        MockMultipartFile pngMockMultipartFile = getPngMockMultipartFile("src/main/resources/test/trouble.png");
//        MockMultipartFile pngMockMultipartFile = getPngMockMultipartFile("src/main/resources/test/oily.png");
        //when
        DiagnosisDto diagnosisDto = new DiagnosisDto(savedMember.getMemberId(), Kind.TROUBLE, pngMockMultipartFile, "male");

        DiagnosisResponse response = diagnosisService.diagnosisSkin(diagnosisDto);

        //then
        Assertions.assertThat(response.getResult()).isEqualTo("troublenormal");
    }

    @Test
    @DisplayName("타입 진단을 수행하면 결과가 반환된다.")
    @Transactional
    public void diagnosisType() throws Exception{
        //given
        skinRepository.save(new Skin(Kind.TYPE, "oily"));
        skinRepository.save(new Skin(Kind.TYPE, "typenormal"));
        skinRepository.save(new Skin(Kind.TYPE, "dry"));
        skinRepository.save(new Skin(Kind.TROUBLE, "troublenormal"));

        Member member = new Member(ROLE_USER, "profile", "user", "nick");
        Member savedMember = memberRepository.save(member);

        MockMultipartFile pngMockMultipartFile = getPngMockMultipartFile("src/main/resources/test/tttt.jpeg");
        //when
        DiagnosisDto diagnosisDto = new DiagnosisDto(savedMember.getMemberId(), Kind.TYPE, pngMockMultipartFile, "male");

        DiagnosisResponse response = diagnosisService.diagnosisSkin(diagnosisDto);
        System.out.println("response.getResult() = " + response.getResult());

        //then
        Assertions.assertThat(response.getResult()).isEqualTo("typenormal");
    }
}