package com.example.demo.repository;

import com.example.demo.entry.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoRep extends MongoRepository<User, String> {

    User findByNickname(String nickname);

}
