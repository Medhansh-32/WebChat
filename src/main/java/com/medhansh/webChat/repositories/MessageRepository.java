package com.medhansh.webChat.repositories;

import com.medhansh.webChat.entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository< Message, ObjectId> {

}
