package firstskin.firstskin.dianosis.api.controller;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.domain.Role;
import firstskin.firstskin.member.repository.MemberRepository;
import firstskin.firstskin.skin.Kind;
import firstskin.firstskin.skin.Skin;
import firstskin.firstskin.skin.repository.SkinRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DiagnosisControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkinRepository skinRepository;

    @Test
    @DisplayName("피부 진단 테스트를 실행하면 진단 결과가 반환된다.")
    @Transactional
//    @Rollback(value = false)
    public void diagnosisSkin() throws Exception {
        //given
        Optional<Skin> optionalSkin = skinRepository.findByResult("dry");
        Skin skin;

        if (optionalSkin.isPresent()) {
            skin = optionalSkin.get();
        } else {
            skin = new Skin(Kind.TYPE, "dry");
            skin = skinRepository.save(skin);
        }

        Member findMember = null;
        findMember = memberRepository.findByUserId("id111");

        if(findMember == null) {
            Member member = new Member(Role.ROLE_USER, "prf1", "id111", "member1Nick");
            findMember = memberRepository.save(member);
        }

        Path drypath = Paths.get("src/main/resources/test/dry.png");
        byte[] dryContent = Files.readAllBytes(drypath);
        MockMultipartFile file = new MockMultipartFile("file", "dry.png", "image/png", dryContent);

        //expected
        assertThat(findMember.getMemberId()).isNotNull();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/skin/diagnosis")
                        .file(file)
                        .param("kind", Kind.TYPE.name())
                        .sessionAttr("memberId", findMember.getMemberId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}
