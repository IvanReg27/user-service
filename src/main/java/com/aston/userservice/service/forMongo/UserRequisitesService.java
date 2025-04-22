package com.aston.userservice.service.forMongo;

import com.aston.userservice.domain.entity.forMongo.UserRequisites;
import com.aston.userservice.repository.forMongo.UserRequisitesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRequisitesService {
    private final UserRequisitesRepository userRequisitesRepository;

    public UserRequisites save(UserRequisites doc) {
        return userRequisitesRepository.save(doc);
    }
}
