package com.medhansh.webChat.controllers;

import com.medhansh.webChat.entity.Contact;
import com.medhansh.webChat.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/new")
    public ResponseEntity<String> addContact(@RequestParam String userName) {

        String username=contactService.saveContact(userName);
        if(username!=null){
            return new ResponseEntity<>("New Contact "+userName+" Saved", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("New Contact "+userName+" Not Saved", HttpStatus.BAD_REQUEST);
        }

    }
}
