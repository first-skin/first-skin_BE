package firstskin.firstskin.common.component;

import firstskin.firstskin.common.constants.HttpMethod;
import firstskin.firstskin.member.domain.Role;
import firstskin.firstskin.user.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static firstskin.firstskin.common.constants.HttpMethod.*;
import static firstskin.firstskin.member.domain.Role.*;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final MemberService memberService;
    private final Map<String, Map<HttpMethod, Role>> uriRoleMap;

    public AuthInterceptor(MemberService memberService) {
        this.memberService = memberService;
        this.uriRoleMap = new LinkedHashMap<>();
        configureUriRoleMap();
    }

    private void configureUriRoleMap() {
        //진단
        addUri("/api/skin/diagnosis", POST, ROLE_USER)
                //관리자
                .addUri("/api/admin", GET, ROLE_ADMIN)
                .addUri("/api/admin", POST, ROLE_ADMIN)
                .addUri("/api/admin", DELETE, ROLE_ADMIN)
                .addUri("/api/admin", PUT, ROLE_ADMIN)
                // 유저 정보 수정
                .addUri("/api/members", PUT, ROLE_ADMIN)
                .addUri("/api/members", PUT, ROLE_USER)
                // 로그아웃
                .addUri("/api/logout-kakako", GET, ROLE_USER)
                // 개인별 카테고리 조회
                .addUri("/api/cosmetics/personal", GET, ROLE_USER)
                // 리뷰 작성
                .addUri("/api/reviews", POST, ROLE_USER)
                // 리뷰 삭제
                .addUri("/api/reviews", DELETE, ROLE_USER)
                .addUri("/api/reviews", DELETE, ROLE_ADMIN)
                // 유저별 리뷰 조회
                .addUri("/api/reviews/members", GET, ROLE_USER)
                .addUri("/api/reviews/members", GET, ROLE_ADMIN)
                // 자가 진단
                .addUri("/api/self", GET, ROLE_USER);
    }

    private AuthInterceptor addUri(String uri, HttpMethod method, Role role) {
        uriRoleMap.computeIfAbsent(uri, k -> new HashMap<>()).put(method, role);
        return this;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpMethod method = HttpMethod.fromString(request.getMethod());

        if (method == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 HTTP 메서드입니다.");
            return false;
        }

        for (Map.Entry<String, Map<HttpMethod, Role>> entry : uriRoleMap.entrySet()) {
            if (requestURI.startsWith(entry.getKey())) {
                Role requiredRole = entry.getValue().get(method);
                if (requiredRole != null && !memberService.hasRole(request, requiredRole)) {
                    if (requiredRole == ROLE_USER) {
                        log.error("로그인이 필요합니다");
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
                    } else {
                        log.error("관리자 권한이 필요합니다");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 필요합니다.");
                    }
                    return false;
                }
                break;
            }
        }

        return true;
    }
}
