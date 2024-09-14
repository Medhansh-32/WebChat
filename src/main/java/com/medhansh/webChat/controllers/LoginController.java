package com.medhansh.webChat.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class LoginController {

//    @PostMapping("/login")
//    public String login(HttpServletRequest request,HttpServletResponse response, @RequestBody String username) {
//        String userId = URLEncoder.encode(username, StandardCharsets.UTF_8);
//        HttpSession httpSession=request.getSession();
//        httpSession.setAttribute("userId",userId);
//        Cookie cookie=new Cookie("userId",userId);
//        response.addCookie(cookie);
//
//        return "User " + username + " logged in successfully";
//    }
@PostMapping("/login")
public String login(HttpServletRequest request, HttpServletResponse response, @RequestBody String username) {
    String userId = URLEncoder.encode(username, StandardCharsets.UTF_8);

    // Create session and set userId
    HttpSession session = request.getSession();
    session.setAttribute("userId", userId);

    // Add userId as a cookie
    Cookie cookie = new Cookie("userId", userId);
    response.addCookie(cookie);

    return "User " + username + " logged in successfully";
}

}
