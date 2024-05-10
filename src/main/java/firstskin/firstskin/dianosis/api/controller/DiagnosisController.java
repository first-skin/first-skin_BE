package firstskin.firstskin.dianosis.api.controller;

import firstskin.firstskin.dianosis.api.request.DiagnosisDto;
import firstskin.firstskin.dianosis.api.response.DiagnosisResponse;
import firstskin.firstskin.dianosis.service.DiagnosisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skin/diagnosis")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping
    public DiagnosisResponse diagnosisSkin(DiagnosisDto diagnosisDto, HttpServletRequest request) throws IOException {
        diagnosisDto.setMemberId((Long) request.getSession().getAttribute("memberId"));
        return diagnosisService.diagnosisSkin(diagnosisDto);
    }
}
