package com.smartqueue.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class PageController {
 @GetMapping({"/","/dashboard"}) public String dashboard(){return "dashboard";}
 @GetMapping("/join") public String join(){return "join";}
 @GetMapping("/track") public String track(){return "track";}
}
