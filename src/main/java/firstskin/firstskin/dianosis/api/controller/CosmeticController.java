package firstskin.firstskin.dianosis.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import firstskin.firstskin.common.exception.UnauthorizedException;
import firstskin.firstskin.dianosis.api.request.CosmeticPersonal;
import firstskin.firstskin.dianosis.api.request.CosmeticRequest;
import firstskin.firstskin.dianosis.api.response.CosmeticPageResponse;
import firstskin.firstskin.dianosis.api.response.PersonalResult;
import firstskin.firstskin.dianosis.service.CosmeticService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cosmetics")
@Slf4j
public class CosmeticController {

    private final CosmeticService cosmeticService;

    @GetMapping
    public CosmeticPageResponse searchCosmetics(CosmeticRequest request) throws JsonProcessingException {

        log.info("request : {}", request);
        return cosmeticService.searchCosmetics(request);

    }

    @GetMapping("/personal")
    public CosmeticPageResponse searchPersonalCosmetics(HttpSession session, CosmeticPersonal request) throws JsonProcessingException {

        if (session == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        log.info("화장품 검색 memberId : {}", memberId);
        return cosmeticService.searchPersonalCosmetics(memberId, request);

    }

    @GetMapping("/personal/results")
    public PersonalResult getPersonalResults(HttpSession session) {

        if (session == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return cosmeticService.getPersonalResults(memberId);
    }
}
