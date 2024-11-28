package com.medhansh.webChat.controller;

import com.medhansh.webChat.model.ChatMessage;
import com.medhansh.webChat.repository.ChatMessageRepository;
import com.medhansh.webChat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
public class ChatController {


    private ChatMessageRepository chatMessageRepository;
    private SimpMessagingTemplate messagingTemplate;
    private ChatService chatService;

    @Autowired
    ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageRepository chatMessageRepository, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.chatService = chatService;
    }

    @MessageMapping("/private-message")
    public ChatMessage sendPrivateMessage(@Payload ChatMessage message) {
        // Get the sender's username from session attributes
        message.setTimestamp(LocalDateTime.now());
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
        return chatMessageRepository.save(message);
    }

    @GetMapping("/messages/{user2}")
    public List<ChatMessage> getMessages(@PathVariable String user2) {
        return chatService.getMessages(user2);
    }


}
