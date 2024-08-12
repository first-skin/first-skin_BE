package firstskin.firstskin.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = {"/", "/login", "/memberlist","/category", "/restudy", "/logout", "MemberList"})
    public String forwardMain() {
        return "forward:/index.html";
    }

}
