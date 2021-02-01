package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@Slf4j
public class HomeController {

    // 롬복으로 대체 함
    // Logger log = LoggerFactory..

    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home";
    }
}
