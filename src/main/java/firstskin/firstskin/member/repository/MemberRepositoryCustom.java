package firstskin.firstskin.member.repository;

import firstskin.firstskin.admin.api.dto.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    Page<MemberResponse> getMembers(Pageable pageable);
}
