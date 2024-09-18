package com.medhansh.webChat.service;

import com.medhansh.webChat.entity.Contact;
import com.medhansh.webChat.entity.Message;
import com.medhansh.webChat.entity.User;
import com.medhansh.webChat.repositories.ContactRespository;
import com.medhansh.webChat.repositories.MessageRepository;
import com.medhansh.webChat.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
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

    public boolean addNewUser(String userName){

        try {
            userRepository.save(User.builder()
                    .username(userName)
                    .contactList(new ArrayList<>()).build());
        }catch (Exception e){
            log.error("User Exists");
            return false;
        }
    return true;
    }


    public boolean addNewContactToUser(String userName,String newContact){
        User user=userRepository.findByUsername(userName);
        ArrayList<Contact> contacts=user.getContactList();
        contacts.add(Contact
                .builder()
                .userName(newContact)
                .messages(new ArrayList<>()).build());
        user.setContactList(contacts);
        try {
            userRepository.save(user);
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
        return true;
    }


}
