package firstskin.firstskin.dianosis.api.controller;

import firstskin.firstskin.dianosis.DiagnosisRepository;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CosmeticControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkinRepository skinRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Test
    @DisplayName("개인별 화장품 검색 테슽트")
    @Transactional
    public void searchPersonal() throws Exception{
        //given
        Member member = new Member(Role.ROLE_USER, "프로필이미지", "윺저아이디", "니그네임");
        Member member1 = new Member(Role.ROLE_USER, "프로필이미지", "유저아이디", "닉네임");
        memberRepository.save(member1);

        Skin skin = new Skin(Kind.TYPE, "oily");

        Skin savedSkin = skinRepository.save(skin);
        Member savedMember = memberRepository.save(member);

        Diagnosis build = Diagnosis.builder()
                .member(savedMember)
                .skin(savedSkin)
                .skinPictureUrl("https://naver.com")
                .build();
        Diagnosis saveDiagnosis = diagnosisRepository.save(build);



        //when

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cosmetics/personal")
                        .param("size", "10")
                        .param("sort", "asc")
                        .param("category", "클렌징")
                        .param("page", "2")
                        .sessionAttr("memberId", savedMember.getMemberId())
                        .sessionAttr("role", savedMember.getRole()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("화장품 검색")
    public void searchCosmetic() throws Exception{
        //given

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cosmetics")
                        .param("size", "10")
                        .param("sort", "asc")
                        .param("category", "스킨/로션")
                        .param("page", "1")
                        .param("query", "지성")
                        .param("kind", "TYPE"))
                .andDo(MockMvcResultHandlers.print());
    }

}