package com.medhansh.webChat.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/message")
    public String showMessage() {
         return "temp";
     }
}
