package com.medhansh.webChat.controller;

import com.medhansh.webChat.model.ChatMessage;
import com.medhansh.webChat.repository.ChatMessageRepository;
import com.medhansh.webChat.service.ChatService;
import com.medhansh.webChat.service.ImageUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@Slf4j
public class ChatController {


    private ChatMessageRepository chatMessageRepository;
    private SimpMessagingTemplate messagingTemplate;
    private ChatService chatService;
    private ImageUploadService imageUploadService;

    @Autowired
    ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageRepository chatMessageRepository, ChatService chatService, ImageUploadService imageUploadService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.chatService = chatService;
        this.imageUploadService=imageUploadService;
    }

    @PostMapping("/image/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
       return imageUploadService.uploadImage(file);
    }

    @MessageMapping("/private-message")
    public ChatMessage sendPrivateMessage(@Payload ChatMessage message,Principal principal) {

        message.setSender(principal.getName());
        String timeZone = "Asia/Kolkata";
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone));
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
            message.setTimestamp(localDateTime);
            messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
            System.out.println(message);
            return chatMessageRepository.save(message);
    }

    @GetMapping("/messages/{user2}")
    public List<ChatMessage> getMessages(@PathVariable String user2) {
        return chatService.getMessages(user2);
    }
    @GetMapping("/image/download")
    public ResponseEntity<ByteArrayResource> downloadImage(@RequestParam("imageUrl" )String url) throws IOException {
   if (url!=null){
       try {
           return new ResponseEntity<>(chatService.downloadImage(url),HttpStatus.OK);
       }catch (Exception e){
           return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
       }
   }else{
       return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
   }


    }

}
