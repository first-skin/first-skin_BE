package firstskin.firstskin.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @GetMapping("/admin")
    public String forwardAdmin() {
        return "forward:/web/admin/index.html";  // URL을 유지하면서 내부적으로 전달
    }

    @GetMapping("/")
    public String forwardMain() {
        return "forward:/web/index.html";
    }

    @RequestMapping(value = "/{path:^(?!api|web|admin|swagger).*}/**")
    public String redirect() {
        // 모든 비정적 파일 요청을 index.html로 리디렉션
        return "forward:/web/admin/index.html";
    }
}
