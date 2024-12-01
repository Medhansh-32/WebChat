package com.medhansh.webChat.controller;


import com.medhansh.webChat.model.User;
import com.medhansh.webChat.repository.UserRepository;
import com.medhansh.webChat.service.ImageUploadService;
import com.medhansh.webChat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public List<String> getAllUsers() {
        return userService.getAllUsers();
    }

        @PostMapping("/register")
        public ResponseEntity createUser(@ModelAttribute User user,
                @RequestParam(value = "profilePicture", required = false) MultipartFile file) {

            Boolean flag=userService.createUser(user,file);
        if(flag==true){
            return new ResponseEntity(HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping("/addContact")
    public ResponseEntity addContact(@RequestBody Map<String, String> requestBody) {
        log.info(requestBody.get("newContact"));
        if(userService.addContact(requestBody.get("newContact"))){
            return new ResponseEntity(HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }
}
