package com.aston.userservice.controller.forMongo;

import com.aston.userservice.domain.entity.forMongo.UserRequisites;
import com.aston.userservice.service.forMongo.UserRequisitesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mongo")
@RequiredArgsConstructor
public class UserRequisitesController {

    private final UserRequisitesService userRequisitesService;

    @PostMapping("/user")
    public UserRequisites saveUser(@RequestBody UserRequisites userRequisites) {
        return userRequisitesService.save(userRequisites);
    }
}
