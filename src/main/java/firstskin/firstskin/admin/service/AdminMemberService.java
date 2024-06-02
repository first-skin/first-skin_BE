package firstskin.firstskin.admin.service;

import firstskin.firstskin.admin.api.dto.response.MemberResponse;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public Page<MemberResponse> getMembers(Pageable pageable) {
        return memberRepository.getMembers(pageable);
    }

    @Transactional
    public void inactiveMember(Long memberId) {
        Member findMember = memberRepository
                .findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        findMember.delete();
    }
}
