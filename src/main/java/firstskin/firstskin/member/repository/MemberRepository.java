package firstskin.firstskin.member.repository;

import firstskin.firstskin.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Member findByUserId(String userId);

    void deleteByUserId(String id111);
}
