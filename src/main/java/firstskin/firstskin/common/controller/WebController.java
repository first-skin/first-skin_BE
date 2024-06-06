package firstskin.firstskin.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {


    @GetMapping(value = {"/", "/login", "/MemberList","/category", "/restudy", "/logout"})
    public String forwardMain() {
        return "forward:/index.html";
    }

}
