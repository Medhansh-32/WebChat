package com.medhansh.webChat.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;

@Data
@Document(collection = "Message")
public class Message {

    @Id
    private String id;
    private String sender;
    private String receiver;
    private Date date;
    private String message;

}
