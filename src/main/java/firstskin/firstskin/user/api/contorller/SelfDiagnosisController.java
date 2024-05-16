package firstskin.firstskin.user.api.contorller;

import firstskin.firstskin.dianosis.domain.Diagnosis;
import firstskin.firstskin.dianosis.service.SelfDiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/self")
public class SelfDiagnosisController {
    private final SelfDiagnosisService diagnosisService;

    @GetMapping("/diagnosis")
    public Diagnosis getDiagnosisByDate(@RequestParam("date") LocalDate date) {
        return diagnosisService.getDiagnosisByDate(date);
    }
}
