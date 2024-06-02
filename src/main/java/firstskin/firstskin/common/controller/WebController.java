package firstskin.firstskin.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/admin")
    public String forwardAdmin() {
        return "forward:/web/admin/index.html";  // URL을 유지하면서 내부적으로 전달
    }
}
