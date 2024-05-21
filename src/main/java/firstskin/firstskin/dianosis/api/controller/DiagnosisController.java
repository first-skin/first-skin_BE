package firstskin.firstskin.dianosis.api.controller;

import firstskin.firstskin.common.exception.UnauthorizedException;
import firstskin.firstskin.dianosis.api.request.DiagnosisDto;
import firstskin.firstskin.dianosis.api.response.DiagnosisResponse;
import firstskin.firstskin.dianosis.service.DiagnosisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/skin/diagnosis")
@Slf4j
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping
    public DiagnosisResponse diagnosisSkin(DiagnosisDto diagnosisDto, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession(false);
        log.info("진단 요청 session : {}", session);
        if (session == null) {
            throw new UnauthorizedException();
        }
        Object memberId = session.getAttribute("memberId");
        log.info("진단 요청 memberId : {}", memberId);
        if (memberId == null) {
            throw new UnauthorizedException();
        }
        diagnosisDto.setMemberId((Long) memberId);
        return diagnosisService.diagnosisSkin(diagnosisDto);
    }
}
