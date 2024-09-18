package com.medhansh.webChat.service;

import com.medhansh.webChat.entity.Contact;
import com.medhansh.webChat.entity.Message;
import com.medhansh.webChat.repositories.ContactRespository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);
    private ContactRespository contactRespository;
    @Autowired
    public ContactService(ContactRespository contactRespository) {

        this.contactRespository = contactRespository;
    }


    public String saveContact(String userName) {
        Contact contact =new Contact();
        contact.setUserName(userName);
        contact.setMessages(null);
        try {
            contactRespository.save(contact);
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
               return userName;
            }
}
