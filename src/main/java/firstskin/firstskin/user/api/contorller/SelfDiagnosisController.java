package firstskin.firstskin.user.api.contorller;

import firstskin.firstskin.dianosis.domain.Diagnosis;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.user.api.dto.SelfDiagnosisDto;
import firstskin.firstskin.user.service.MemberService;
import firstskin.firstskin.user.service.SelfDiagnosisService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/self")
public class SelfDiagnosisController {

    private final SelfDiagnosisService diagnosisService;
    private final MemberService memberService;



    @GetMapping()
    public List<SelfDiagnosisDto> getDiagnosisByDate(HttpSession session, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long memberId = (Long) session.getAttribute("memberId");
        Optional<Member> member = memberService.findMemberById(memberId);
        return diagnosisService.getDiagnosisByDate(member, date);
    }


}
