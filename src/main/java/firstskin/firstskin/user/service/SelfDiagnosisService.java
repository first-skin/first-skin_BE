package firstskin.firstskin.dianosis.service;

import firstskin.firstskin.dianosis.DiagnosisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import firstskin.firstskin.dianosis.domain.Diagnosis;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Transactional(readOnly = true)
    public Diagnosis getDiagnosisByDate(LocalDate date) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(1).minusNanos(1); // 해당 날짜의 끝 시간
        return diagnosisRepository.findByCreatedDate(startDate, endDate);
    }
}