package com.medhansh.webChat.controllers;

import com.medhansh.webChat.entity.User;
import com.medhansh.webChat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/adduser")
    public ResponseEntity<String> getUsers(@RequestParam("newuser") String newUser) {
       boolean check=userService.addNewUser(newUser);

       if(check) {
           return new ResponseEntity<>("User added", HttpStatus.OK);
       }else{
           return new ResponseEntity<>("User not added", HttpStatus.BAD_REQUEST);
       }
    }

    @GetMapping("/contact")
    public ResponseEntity<String> addContactToUser(@RequestParam("user") String username,@RequestParam("newContact") String newContact){
        boolean check=userService.addNewContactToUser(username,newContact);
        if(check==true){
            return new ResponseEntity<>("new contact : "+newContact+" saved in "+username+" contact list", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("new contact : "+newContact+" not saved in "+username+" contact list", HttpStatus.BAD_REQUEST);
        }
    }
}
