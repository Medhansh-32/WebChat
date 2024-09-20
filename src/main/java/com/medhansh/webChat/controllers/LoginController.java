package com.medhansh.webChat.controllers;

import com.medhansh.webChat.entity.Contact;
import com.medhansh.webChat.repositories.UserRepository;
import com.medhansh.webChat.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final UserRepository userRepository;
    private final UserService userService;

    public LoginController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        String username = request.getParameter("username");
        String userId = URLEncoder.encode(username, StandardCharsets.UTF_8);
        username = userId.replace('+', ' ');
        log.info(username);

        // Create session and set userId
        HttpSession session = request.getSession();
        session.setAttribute("userId", username);

        // Create a cookie with the correct attributes
        Cookie cookie = new Cookie("userId", userId);
        cookie.setSecure(true);  // Ensure it's sent over HTTPS
        cookie.setPath("/"); // Set the path for which the cookie is valid

        response.addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Secure; SameSite=None");
        response.addCookie(cookie);

        // Populate the contact list
        List<Contact> contactList = userService.getContactList(username);
        model.addAttribute("contactList", contactList);
        return "contactList";
    }

    @GetMapping("/receiver")
    public String receiver(HttpServletRequest request, HttpServletResponse response, @RequestParam("receiver") String receiver) throws IOException {
         receiver = URLEncoder.encode(receiver, StandardCharsets.UTF_8);
        Cookie cookie = new Cookie("receiver", receiver);
        cookie.setSecure(true);
        response.addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Secure; SameSite=None");
        response.addCookie(cookie);
        return "temp";
    }

    @GetMapping("/addcontact")
    public ResponseEntity<String> addContact(@RequestParam("username") String username,
                                             @RequestParam("receiver") String receiver) {
        boolean check = userService.addNewContactToUser(username, receiver);
        if (check) {
            return new ResponseEntity<>("Contact Added", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Contact Not Added", HttpStatus.BAD_REQUEST);
        }
    }
}
