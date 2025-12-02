package dev.backend.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 頁面路由控制器
 */
@Controller
public class PageController {
    
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
    
    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }
    
    @GetMapping("/register")
    public String register() {
        return "forward:/register.html";
    }
}
