package firstskin.firstskin.member.repository;

import firstskin.firstskin.admin.api.dto.response.MemberResponse;
import firstskin.firstskin.dianosis.DiagnosisRepository;
import firstskin.firstskin.dianosis.domain.Diagnosis;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.domain.Role;
import firstskin.firstskin.skin.Kind;
import firstskin.firstskin.skin.Skin;
import firstskin.firstskin.skin.repository.SkinRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberRepositoryImplTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DiagnosisRepository diagnosisRepository;

    @Autowired
    SkinRepository skinRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("회원 목록 조회시 퍼스널컬러, 타입, 트러블 결과도 함께 조회된다")
    @Transactional
    public void getMembers() throws Exception {
        //given
        Member member1 = new Member(Role.ROLE_USER, "프로필사진1", "아이디1", "닉네임1");
        Member member2 = new Member(Role.ROLE_USER, "프로필사진2", "아이디2", "닉네임2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Skin ps1 = new Skin(Kind.PERSONAL_COLOR, "봄웜");
        Skin ps2 = new Skin(Kind.PERSONAL_COLOR, "가을쿨");
        Skin ps3 = new Skin(Kind.PERSONAL_COLOR, "겨울웜");
        Skin ps4 = new Skin(Kind.PERSONAL_COLOR, "여름쿨");

        Skin ts1 = new Skin(Kind.TYPE, "드라이");
        Skin ts2 = new Skin(Kind.TYPE, "지성");
        Skin ts3 = new Skin(Kind.TYPE, "복합성");

        Skin tt1 = new Skin(Kind.TROUBLE, "여드름");
        Skin tt2 = new Skin(Kind.TROUBLE, "각질");
        Skin tt3 = new Skin(Kind.TROUBLE, "건조함");

        skinRepository.save(ps1);
        skinRepository.save(ps2);
        skinRepository.save(ps3);
        skinRepository.save(ps4);
        skinRepository.save(ts1);
        skinRepository.save(ts2);
        skinRepository.save(ts3);
        skinRepository.save(tt1);
        skinRepository.save(tt2);
        skinRepository.save(tt3);


        Diagnosis 진단1 = Diagnosis.builder()
                .member(member1)
                .skin(ps1)
                .skinPictureUrl("사진1")
                .build();

        Diagnosis 진단2 = Diagnosis.builder()
                .member(member1)
                .skin(ts2)
                .skinPictureUrl("사진2")
                .build();

        Diagnosis 진단3 = Diagnosis.builder()
                .member(member1)
                .skin(tt1)
                .skinPictureUrl("사진3")
                .build();

        Diagnosis 진단66 = Diagnosis.builder()
                .member(member2)
                .skin(tt3)
                .skinPictureUrl("사진3")
                .build();

        Diagnosis 진단4 = Diagnosis.builder()
                .member(member2)
                .skin(ps4)
                .skinPictureUrl("사진4")
                .build();

        Diagnosis 진단5 = Diagnosis.builder()
                .member(member2)
                .skin(ts3)
                .skinPictureUrl("사진5")
                .build();

        Diagnosis 진단6 = Diagnosis.builder()
                .member(member2)
                .skin(tt2)
                .skinPictureUrl("사진6")
                .build();

        diagnosisRepository.save(진단1);
        diagnosisRepository.save(진단2);
        diagnosisRepository.save(진단3);
        diagnosisRepository.save(진단4);
        diagnosisRepository.save(진단5);
        diagnosisRepository.save(진단6);
        diagnosisRepository.save(진단66);
        //when

        Pageable pageable = Pageable.ofSize(10).withPage(0);

        Page<MemberResponse> members = memberRepository.getMembers(pageable);

        //then
        members.forEach(System.out::println);

    }

}