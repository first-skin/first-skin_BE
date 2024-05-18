package firstskin.firstskin.user.api.contorller;

import firstskin.firstskin.dianosis.domain.Diagnosis;
import firstskin.firstskin.user.service.SelfDiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/self")
public class SelfDiagnosisController {

    private final SelfDiagnosisService diagnosisService;


    @PostMapping("/diagnosis")
    public Diagnosis saveDiagnosis(@RequestBody Diagnosis diagnosis) {
        return diagnosisService.saveDiagnosis(diagnosis);
    }

    @GetMapping("/diagnosis")
    public Diagnosis getDiagnosisByDate(@RequestParam("date") LocalDate date) {
        Optional<Diagnosis> optionalDiagnosis = diagnosisService.getDiagnosisByDate(date);
        return optionalDiagnosis.orElse(null);
    }


}
