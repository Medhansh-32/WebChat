package com.medhansh.webChat.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "Contact")
public class Contact {

    @Id
    private String id;
    private String userName;
    private ArrayList<Message> messages=new ArrayList<>();
}
