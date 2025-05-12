package ru.makhorin.userservice.constants;

import ru.makhorin.userservice.domain.entity.User;
import ru.makhorin.userservice.security.Role;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

public class TestConstantsUser {

    public static final String LOGIN_NAME = "ivan";
    public static final User USER = User.builder()
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
}
