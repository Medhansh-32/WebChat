package com.medhansh.webChat.service;

import com.medhansh.webChat.model.ChatMessage;
import com.medhansh.webChat.model.User;
import com.medhansh.webChat.repository.ChatMessageRepository;
import com.medhansh.webChat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@Slf4j
public class ChatService {

    private final UserRepository userRepository;
    private ChatMessageRepository chatMessageRepository;
    public ChatService(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }
    
    public List<ChatMessage> getMessages(@PathVariable String user2) {
        String user1 = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info(user1);
        log.info(user2);
        return chatMessageRepository.findMessagesBetweenUsers(user1, user2);
    }


}
