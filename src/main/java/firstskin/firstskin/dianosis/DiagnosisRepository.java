package firstskin.firstskin.dianosis;

import firstskin.firstskin.dianosis.domain.Diagnosis;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.skin.Kind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findTopByMemberAndSkinKindAndCreatedDateBetweenOrderByCreatedDateDesc(Member member, Kind kind, LocalDateTime startDate, LocalDateTime endDate);
}
