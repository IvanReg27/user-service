package com.aston.userservice.event.usercreated;

import com.aston.userservice.security.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


/**
 * Класс для создания события для Kafka
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserCreatedEvent {

    private String userId;

    private String firstName;

    private String lastName;

    private LocalDate birthday;

    private String inn;

    private String snils;

    private String passportNumber;

    private String login;

    private String password;

    private List<String> roles;
}
