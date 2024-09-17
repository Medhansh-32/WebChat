package com.medhansh.webChat.service;

import com.medhansh.webChat.entity.Message;
import com.medhansh.webChat.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Boolean saveMessage(String sender, String receiver, String message) {
       Message messageObj= Message.builder().sender(sender)
                .receiver(receiver)
                .message(message)
                .build();

        Message messageObj1= Message.builder().sender(receiver)
                .receiver(sender)
                .message(message)
                .build();
try {
    messageRepository.save(messageObj1);
    messageRepository.save(messageObj);
}catch (Exception e){
    return false;
}

        return true;
    }
}
