package firstskin.firstskin.member.repository;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUserId(String userId);
    List<Member> findByRole(Role role);

    void deleteByUserId(String id111);
}
