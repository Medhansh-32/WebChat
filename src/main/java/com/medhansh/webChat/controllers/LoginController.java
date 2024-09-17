package com.medhansh.webChat.controllers;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@WebFilter("/signup")
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
//  }
@GetMapping("/login")
public String login(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String username=request.getParameter("username");
    String userId = URLEncoder.encode(username, StandardCharsets.UTF_8);

    // Create session and set userId
    HttpSession session = request.getSession();
    session.setAttribute("userId", userId);

    // Add userId as a cookie
    // Create the userId cookie and manually set SameSite=None


// Create a cookie with the correct attributes
    Cookie cookie = new Cookie("userId", userId);
    cookie.setSecure(true);  // Ensure it's sent over HTTPS
 //   cookie.setHttpOnly(true); // Prevent access from JavaScript
    cookie.setPath("/"); // Set the path for which the cookie is valid

// Manually set SameSite=None attribute
    response.addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue()
            + "; Secure; HttpOnly; SameSite=None");

    response.addCookie(cookie);
    return "temp";

}
@GetMapping("/receiver")
    public String receiver(HttpServletRequest request, HttpServletResponse response,@RequestParam("receiver") String receiver) throws IOException {

    Cookie cookie = new Cookie("receiver", receiver);
    cookie.setSecure(true);
    response.addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue()
            + "; Secure; HttpOnly; SameSite=None");

    return "temp";
}
}
