package firstskin.firstskin.admin.service;

import firstskin.firstskin.admin.api.dto.response.MemberResponse;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AdminMemberServiceTest {

    @Autowired
    AdminMemberService adminMemberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    SkinRepository skinRepository;

    @Autowired
    DiagnosisRepository diagnosisRepository;

    @Test
    @DisplayName("회원 목록 조회시 퍼스널컬러, 타입, 트러블 결과도 함께 조회된다")
    @Transactional
    public void getMembers() throws Exception{
        //given
        Member member1 = new Member(Role.ROLE_USER, "프로필사진1", "아이디1", "닉네임1");
        Member member2 = new Member(Role.ROLE_USER, "프로필사진2", "아이디2", "닉네임2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Skin ps1 = new Skin(Kind.PERSONAL_COLOR, "봄웜");
        Skin ps2 = new Skin(Kind.PERSONAL_COLOR, "가을쿨");

        Skin ts1 = new Skin(Kind.TYPE, "드라이");
        Skin ts2 = new Skin(Kind.TYPE, "지성");

        Skin tt1 = new Skin(Kind.TROUBLE, "여드름");
        Skin tt2 = new Skin(Kind.TROUBLE, "각질");

        skinRepository.save(ps1);
        skinRepository.save(ps2);
        skinRepository.save(ts1);
        skinRepository.save(ts2);
        skinRepository.save(tt1);
        skinRepository.save(tt2);

        Diagnosis diagnosis1 = new Diagnosis(member1, ps1, "사진1");
        Diagnosis diagnosis2 = new Diagnosis(member1, ts1, "사진2");
        Diagnosis diagnosis3 = new Diagnosis(member1, tt1, "사진2");

        Diagnosis diagnosis4 = new Diagnosis(member2, ps2, "사진2");
        Diagnosis diagnosis5 = new Diagnosis(member2, ts2, "사진2");
        Diagnosis diagnosis6 = new Diagnosis(member2, tt2, "사진2");

        diagnosisRepository.save(diagnosis1);
        diagnosisRepository.save(diagnosis2);
        diagnosisRepository.save(diagnosis3);
        diagnosisRepository.save(diagnosis4);
        diagnosisRepository.save(diagnosis5);
        diagnosisRepository.save(diagnosis6);

        //when
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        Page<MemberResponse> members = adminMemberService.getMembers(pageable);

        //then
//        assertThat(members.getContent().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("회원 비활성화시 activated가 false로 변경된다.")
    @Transactional
    public void inactiveMember() throws Exception{
        //given
        String uuid = UUID.randomUUID().toString();
        Member member = new Member(Role.ROLE_USER, "프로필사진1", uuid, "닉네임1");
        Member savedMember = memberRepository.save(member);

        //when

        //전
        Member byUserId1 = memberRepository.findByUserId(savedMember.getUserId());
        assertThat(byUserId1.isActivated()).isTrue();

        adminMemberService.inactiveMember(savedMember.getMemberId());
        Member byUserId = memberRepository.findByUserId(savedMember.getUserId());

        //then
        assertThat(byUserId.isActivated()).isFalse();


    }
}