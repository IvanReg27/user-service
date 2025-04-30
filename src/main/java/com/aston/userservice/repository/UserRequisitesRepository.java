package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.UserRequisites;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Репозиторий для работы с MongoDB
@Repository
public interface UserRequisitesRepository extends MongoRepository<UserRequisites, String> {
    Optional<UserRequisites> findByUserId(String userId);
}
