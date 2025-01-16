package com.aston.userservice.service;

import com.aston.userservice.constants.TestConstantsUser;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.integration.cofig.IntegrationTest;
import com.aston.userservice.integration.cofig.PostgresTestContainer;
import com.aston.userservice.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class UserServiceTest extends PostgresTestContainer {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByLogin() {
        User savedUser = userRepository.save(TestConstantsUser.USER);
        User ivan = userService.findByLogin(TestConstantsUser.LOGIN_NAME);
        Assertions.assertEquals(savedUser.getLogin(), ivan.getLogin());
    }
}
