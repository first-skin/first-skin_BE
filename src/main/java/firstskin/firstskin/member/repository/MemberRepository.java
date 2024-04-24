package firstskin.firstskin.member.repository;

import firstskin.firstskin.category.domain.Category;
import firstskin.firstskin.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
