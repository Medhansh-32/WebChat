package com.medhansh.webChat.service;

import com.medhansh.webChat.model.User;
import com.medhansh.webChat.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserService {


    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final ImageUploadService imageUploadService;
    private UserRepository userRepository;


    private final String defaultProfilePicture;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, @Value("${default_profile_picture}") String defaultProfilePicture, ImageUploadService imageUploadService) {
        this.userRepository = userRepository;
        this.defaultProfilePicture = defaultProfilePicture;
        this.imageUploadService = imageUploadService;
    }

    public List<String> getAllUsers() {
        String username=SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepository.findByUsername(username);
        return user.getContacts();

    }


    public Boolean createUser(User user, MultipartFile file) {
        try{
          if(file==null){
              user.setProfilePicture(defaultProfilePicture);
          }else{
              String profilePicture=imageUploadService.uploadImage(file);
              user.setProfilePicture(profilePicture);
          }
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
          return true;
        }catch(Exception e){
            log.info(e.getMessage());
            return false;
        }


    }
    public Boolean addContact(String newContact) {
        try {
            String user1 = SecurityContextHolder.getContext().getAuthentication().getName();
            User user=userRepository.findByUsername(user1);

                user.getContacts().add(newContact);
                userRepository.save(user);
                return true;
            }
        catch (Exception e) {
            return false;
        }
    }
}
