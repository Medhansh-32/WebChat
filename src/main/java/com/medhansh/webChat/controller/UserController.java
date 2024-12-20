package com.medhansh.webChat.controller;


import com.medhansh.webChat.model.Contact;
import com.medhansh.webChat.model.Otp;
import com.medhansh.webChat.model.User;
import com.medhansh.webChat.repository.UserRepository;
import com.medhansh.webChat.service.ImageUploadService;
import com.medhansh.webChat.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {


    private final ImageUploadService imageUploadService;
    private UserService userService;

    @Autowired
    public UserController(UserService userService, ImageUploadService imageUploadService) {
        this.userService = userService;
        this.imageUploadService = imageUploadService;
    }

    @GetMapping("/data")
    public List<Contact> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity createUser(@RequestParam String username,
                                     @RequestParam String password,
                                     @RequestParam String email,
                                     @RequestParam(value = "profilePicture", required = false) MultipartFile file, HttpServletResponse response) throws IOException {


        Boolean flag=userService.createUser(User.builder().username(username)
                .password(password).email(email).build(),file);
        if(flag==true){
            return new ResponseEntity(HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping("/addContact")
    public ResponseEntity addContact(@RequestBody Map<String, String> requestData) {
        String newContact = requestData.get("newContact");
        if(userService.addContact(newContact)){
            return new ResponseEntity(HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }
    @DeleteMapping("/deleteContact/{contactName}")
    public ResponseEntity deleteContact(@PathVariable String contactName) {
        log.info(contactName);
        if(userService.deleteContact(contactName)){
            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getOtp")
    public ResponseEntity<Otp> getOtp(@RequestParam String email) throws MessagingException {
        log.info(email);
        return new ResponseEntity<>(userService.sendOtp(email),HttpStatus.OK);
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity verifyOtp(@RequestBody Map<String, String> requestData) {
        if(userService.verifyOtp(requestData.get("email"),requestData.get("otp"))){
            log.info("Data :"+requestData.get("otp"),requestData.get("email"));
            return new ResponseEntity(HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

}