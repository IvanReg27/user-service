package com.aston.userservice.repository.forMongo;

import com.aston.userservice.domain.entity.forMongo.UserRequisites;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRequisitesRepository extends MongoRepository<UserRequisites, String> {
}
