package com.aston.userservice.constants;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.security.Role;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class TestConstantsUser {

    public static final String LOGIN_NAME = "ivan";
    public static final User USER = User.builder()
            .id(UUID.randomUUID())
            .firstName("Иван")
            .lastName("Петров")
            .birthday(LocalDate.of(1990, 1, 1))
            .inn("789856132312")
            .snils("12345678901")
            .passportNumber("0708567890")
            .login(LOGIN_NAME)
            .password("password1")
            .roles(List.of("USER", "ADMIN"))
            .build();
}
