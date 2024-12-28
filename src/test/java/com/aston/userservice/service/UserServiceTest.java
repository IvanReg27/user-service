package com.aston.userservice.service;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.integration.config.IntegrationTest;
import com.aston.userservice.repository.UserRepository;
import com.aston.userservice.security.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@IntegrationTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    private String LOGIN_NAME = "ivan";

    @Autowired
    private UserRepository userRepository;
    private User user = User.builder()
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
