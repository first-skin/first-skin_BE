package firstskin.firstskin.dianosis;

import firstskin.firstskin.dianosis.domain.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
}
