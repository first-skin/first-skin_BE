package firstskin.firstskin.user.api.contorller;

import firstskin.firstskin.dianosis.domain.Diagnosis;
import firstskin.firstskin.user.service.SelfDiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/self")
public class SelfDiagnosisController {

    private final SelfDiagnosisService diagnosisService;



    @GetMapping("/{memberId}")
    public Diagnosis getDiagnosisByDate(@PathVariable Long memberId, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<Diagnosis> optionalDiagnosis = diagnosisService.getDiagnosisByDate(memberId, date);
        return optionalDiagnosis.orElse(null);
    }


}
