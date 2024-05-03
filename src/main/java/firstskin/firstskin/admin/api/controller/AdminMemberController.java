package firstskin.firstskin.admin.api.controller;

import firstskin.firstskin.admin.api.dto.response.MemberResponse;
import firstskin.firstskin.admin.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping
    public Page<MemberResponse> getMembers(Pageable pageable) {
        return adminMemberService.getMembers(pageable);
    }

    @DeleteMapping("/{memberId}")
    public void inactiveMember(@PathVariable Long memberId) {
        adminMemberService.inactiveMember(memberId);
    }
}
