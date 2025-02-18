package com.mysite.ychat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	 @GetMapping("/")
	    public String home() {
	        return "redirect:/main.html"; // 🔹 "/" 요청 시 main.html로 리디렉트
	    }
}
