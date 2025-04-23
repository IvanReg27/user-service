package com.aston.userservice.repository.forMongo;

import com.aston.userservice.domain.entity.forMongo.UserRequisites;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRequisitesRepository extends MongoRepository<UserRequisites, String> {
    Optional<UserRequisites> findByUserId(String userId);
}
