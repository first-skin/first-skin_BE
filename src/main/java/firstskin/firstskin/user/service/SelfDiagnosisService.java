package firstskin.firstskin.user.service;

import firstskin.firstskin.dianosis.DiagnosisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import firstskin.firstskin.dianosis.domain.Diagnosis;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SelfDiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    @Autowired
    public SelfDiagnosisService(DiagnosisRepository diagnosisRepository) {
        this.diagnosisRepository = diagnosisRepository;
    }


    @Transactional
    public Diagnosis saveDiagnosis(Diagnosis diagnosis) {
        return diagnosisRepository.save(diagnosis);
    }

    public Optional<Diagnosis> getDiagnosisByDate(LocalDate date) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(1).minusNanos(1);
        return diagnosisRepository.findByCreatedDateBetween(startDate, endDate);
    }
}