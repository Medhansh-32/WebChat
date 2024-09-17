package com.medhansh.webChat.entity;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;

@Data
@Builder
@Document(collection = "Messages")
public class Message {

    @Id
    private ObjectId id;
    private String sender;
    private String receiver;
    private Date date;
    private String message;

}
