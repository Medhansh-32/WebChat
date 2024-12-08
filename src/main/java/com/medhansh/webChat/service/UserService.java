package com.medhansh.webChat.service;

import com.medhansh.webChat.model.Contact;
import com.medhansh.webChat.model.Otp;
import com.medhansh.webChat.model.OtpVerifiedUsers;
import com.medhansh.webChat.model.User;
import com.medhansh.webChat.repository.UserRepository;
import com.medhansh.webChat.repository.VerifiedUserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {


    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final ImageUploadService imageUploadService;
    private final VerifiedUserRepository verifiedUserRepository;
    private UserRepository userRepository;
    private JavaMailSender javaMailSender;
    private final String defaultProfilePicture;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String emailSecretKey;
    private ConcurrentHashMap<String, Otp> otpStorage = new ConcurrentHashMap<>();

    @Autowired
    public UserService(UserRepository userRepository, @Value("${default_profile_picture}") String defaultProfilePicture, ImageUploadService imageUploadService, JavaMailSender javaMailSender, @Value("${privateKey}") String emailSecretKey, VerifiedUserRepository verifiedUserRepository) {
        this.userRepository = userRepository;
        this.defaultProfilePicture = defaultProfilePicture;
        this.imageUploadService = imageUploadService;
        this.javaMailSender = javaMailSender;
        this.emailSecretKey = emailSecretKey;
        this.verifiedUserRepository = verifiedUserRepository;
    }

    public List<Contact> getAllUsers() {
        String username=SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepository.findByUsername(username);
        System.out.println(user.getContacts());
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
            if(verifiedUserRepository.findOtpVerifiedUsersByOtpVerifiedEmail(user.getEmail())!=null){
                userRepository.save(user);
                return true;
            }else{
                throw new RuntimeException("OTP verification failed");
            }

        }catch(Exception e){
            log.info(e.getMessage());
            return false;
        }


    }
    public Boolean addContact(String newContactName) {
        try {
            String user1 = SecurityContextHolder.getContext().getAuthentication().getName();
            User user=userRepository.findByUsername(user1);
            User newUser=userRepository.findByUsername(newContactName);
            Contact contact=Contact.builder()
                            .contactName(newContactName)
                                    .profilePictureLink(newUser.getProfilePicture()).build();
                user.getContacts().add(contact);
                System.out.println(userRepository.save(user));
                return true;
            }
        catch (Exception e) {
            return false;
        }
    }

    public Boolean deleteContact(String contactName) {
        String user=SecurityContextHolder.getContext().getAuthentication().getName();
        User userData=userRepository.findByUsername(user);
        Boolean isDeleted=userData.getContacts().removeIf(contact -> contact.getContactName().equals(contactName));
        userRepository.save(userData);
        return isDeleted;
    }

    public Otp createOtp(String email){
        String otp="";
        for(int i=0;i<6;i++){
            int digit=(int)(Math.random()*10);
            otp+=digit;
        }
       Otp otpBuild=Otp.builder()
                .email(email)
                .otp(otp)
                .sendTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusMinutes(10)).build();

        otpStorage.put(email+otp,otpBuild);
        return otpBuild;
    }

    public Otp sendOtp(String email) throws MessagingException {
        Otp otpCreated=createOtp(email);
        String otp=otpCreated.getOtp();
        String emailContent="<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Your OTP Code</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #f4f4f4; padding: 20px;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\">\n" +
                "                <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1);\">\n" +
                "                    <tr>\n" +
                "                        <td style=\"font-family: Arial, sans-serif; font-size: 18px; color: #333333;\">\n" +
                "                            <h2 style=\"color: #4CAF50;\">Your OTP Code</h2>\n" +
                "                            <p style=\"color: #333333;\">Dear User,</p>\n" +
                "                            <p style=\"color: #333333;\">Thank you for requesting your OTP. Please use the following code to complete your action:</p>\n" +
                "                            <h3 style=\"font-size: 24px; color: #4CAF50; text-align: center; margin: 20px 0;\">"+otp+"</h3>\n" +
                "                            <p style=\"color: #333333;\">The OTP code is valid for the next 10 minutes. Please do not share it with anyone.</p>\n" +
                "                            <p style=\"color: #333333;\">If you did not request this, please ignore this email.</p>\n" +
                "                            <p style=\"color: #333333;\">Best regards, <br/> "+"Team, WebChat"+"</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>\n";
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Otp Verification");
        mimeMessageHelper.setText(emailContent,true);
        javaMailSender.send(mimeMessage);
        return otpCreated;
    }

    public Boolean verifyOtp(String email,String otp){
        Otp otpStored=otpStorage.get(email+otp);
        if(otpStored==null){
            return false;
        }else{
            otpStorage.remove(email+otp);
            if(otpStored.getExpireTime().isAfter(LocalDateTime.now())){
                verifiedUserRepository.save(OtpVerifiedUsers.builder().otpVerifiedEmail(email).build());
                return true;
            }else{
                return false;
            }
        }
    }
}
