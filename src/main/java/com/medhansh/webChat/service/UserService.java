package com.medhansh.webChat.service;

import com.medhansh.webChat.model.User;
import com.medhansh.webChat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class UserService {


    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<String> getAllUsers() {
        String username=SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepository.findByUsername(username);
        return user.getContacts();

    }


    public User createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public Boolean addContact(String newContact) {
        String user1 = SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepository.findByUsername(user1);
        if(user!=null){
            user.getContacts().add(newContact);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
