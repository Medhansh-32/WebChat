package com.medhansh.webChat.entity;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;

@Data
@Builder
@Document(collection = "Users")
public class User {

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String username;
    @DBRef
    private ArrayList<Contact> contactList=new ArrayList<>();

}
