package com.medhansh.webChat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(collection = "Contacts")
public class Contact {

    @Id
    private ObjectId id;
    private String userName;
    private ArrayList<Message> messages=new ArrayList<>();
}
