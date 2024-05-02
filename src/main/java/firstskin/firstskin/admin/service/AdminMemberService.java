package firstskin.firstskin.admin.service;

import firstskin.firstskin.admin.api.dto.response.MemberResponse;
import firstskin.firstskin.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public Page<MemberResponse> getMembers(Pageable pageable) {
        return memberRepository.getMembers(pageable);
    }
}
