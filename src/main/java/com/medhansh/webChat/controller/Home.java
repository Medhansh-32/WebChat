package com.medhansh.webChat.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {

    @GetMapping
    public String home(Model model) {
        model.addAttribute("username",SecurityContextHolder.getContext().getAuthentication().getName());
        return "index";
    }
    @GetMapping("/register")
    public String register(){
        return "registration";
    }
    @GetMapping("/login")
    public String login() {
        return "login"; // This matches the login.html file
    }
}
