package com.medhansh.webChat.service;

import com.medhansh.webChat.entity.Contact;
import com.medhansh.webChat.entity.Message;
import com.medhansh.webChat.entity.User;
import com.medhansh.webChat.repositories.ContactRespository;
import com.medhansh.webChat.repositories.MessageRepository;
import com.medhansh.webChat.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ContactRespository contactRespository;
    private final MessageRepository messageRepository;

    public UserService(UserRepository userRepository, ContactRespository contactRespository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.contactRespository = contactRespository;
        this.messageRepository = messageRepository;
    }

    public boolean saveUserMessage(String userName,String receiver, Message message){
        User user=userRepository.findByUsername(userName);
        ArrayList<Contact> contacts=user.getContactList();
        for(Contact contact:contacts){
            if(contact.getUserName()==receiver){
                contact.getMessages().add(message);
                Optional<Contact> contact1=contactRespository.findById(contact.getId()) ;
                contact1.get().getMessages().add(message);
                contactRespository.save(contact);
                messageRepository.save(message);
                userRepository.save(user);
                break;
            }
        }
    return true;
    }

}
