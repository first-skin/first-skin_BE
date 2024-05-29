package firstskin.firstskin.member.repository;

import firstskin.firstskin.admin.api.dto.response.MemberResponse;
import firstskin.firstskin.dianosis.api.response.PersonalResult;
import firstskin.firstskin.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    Page<MemberResponse> getMembers(Pageable pageable);

    PersonalResult getPersonalResults(Member member);
}
