package com.medhansh.webChat.repositories;

import com.medhansh.webChat.entity.Contact;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContactRespository extends MongoRepository<Contact, ObjectId> {
    Contact findByUserName(String username);

}
