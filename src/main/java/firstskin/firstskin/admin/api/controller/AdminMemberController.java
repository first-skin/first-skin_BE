package firstskin.firstskin.admin.api.controller;

import firstskin.firstskin.admin.api.dto.response.MemberResponse;
import firstskin.firstskin.admin.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping
    public Page<MemberResponse> getMembers(Pageable pageable) {
        return adminMemberService.getMembers(pageable);
    }
}
