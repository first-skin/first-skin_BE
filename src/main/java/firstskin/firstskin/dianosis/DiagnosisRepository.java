package firstskin.firstskin.dianosis;

import firstskin.firstskin.dianosis.domain.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Diagnosis findByCreatedDate(LocalDateTime startDate, LocalDateTime endDate);
}
