package firstskin.firstskin.dianosis;

import firstskin.firstskin.dianosis.domain.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findTopByMember_MemberIdAndCreatedDateBetweenOrderByCreatedDateDesc(Long memberId, LocalDateTime startDate, LocalDateTime endDate);
}
