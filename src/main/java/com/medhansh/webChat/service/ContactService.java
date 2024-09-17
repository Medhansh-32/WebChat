package com.medhansh.webChat.service;

import com.medhansh.webChat.entity.Contact;
import com.medhansh.webChat.entity.Message;
import com.medhansh.webChat.repositories.ContactRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private ContactRespository contactRespository;
    @Autowired
    public ContactService(ContactRespository contactRespository) {

        this.contactRespository = contactRespository;
    }


    public boolean saveContact(String userName, Message message) {
        Contact contact =new Contact();
        contact.setUserName(userName);
        contact.getMessages().add(message);
        try {
            contactRespository.save(contact);
        }catch (Exception e){
            return false;
        }
                return true;
            }
}
