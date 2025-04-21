package com.aston.userservice.domain.dto;

import com.aston.userservice.security.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDto implements Serializable {
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String inn;
    private String snils;
    private String passportNumber;
    private String login;
    private String password;
    private Set<Role> roles;
}
