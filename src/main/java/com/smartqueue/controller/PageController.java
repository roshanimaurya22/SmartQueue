package com.smartqueue.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

 @GetMapping({"/", "/dashboard"})
 public String dashboard() {
  return "forward:/dashboard.html";
 }

 @GetMapping("/join")
 public String join() {
  return "forward:/join.html";
 }

 @GetMapping("/track")
 public String track() {
  return "forward:/track.html";
 }
}