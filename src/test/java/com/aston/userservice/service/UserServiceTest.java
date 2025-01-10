package com.aston.userservice.service;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.integration.cofig.IntegrationTest;
import com.aston.userservice.integration.cofig.PostgresTestContainer;
import com.aston.userservice.repository.UserRepository;
import com.aston.userservice.security.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@IntegrationTest
class UserServiceTest extends PostgresTestContainer {

    @Autowired
    private UserService userService;
    private final String LOGIN_NAME = "ivan";

    @Autowired
    private UserRepository userRepository;
    private final User user = User.builder()
            .firstName("Иван")
            .lastName("Петров")
            .birthday(LocalDate.of(1990, 01, 01))
            .inn("789856132312")
            .snils("12345678901")
            .passportNumber("0708567890")
            .login(LOGIN_NAME)
            .password("password1")
            .roles(new HashSet<>(List.of(Role.USER, Role.ADMIN)))
            .build();


    @Test
    void findByLogin() {

        User savedUser = userRepository.save(user);

        User ivan = userService.findByLogin(LOGIN_NAME);

        Assertions.assertEquals(savedUser.getLogin(), ivan.getLogin());
    }
}
