package com.aston.userservice.domain.entity;

import com.aston.userservice.security.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Set;

/**
 * Класс сущность, соответствующий таблице User в UserDB
 */
@Table("users")
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class User {

    @Id
    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate birthday;

    private String inn;

    private String snils;

    private String passportNumber;

    private String login;

    private String password;

    @Transient
    @Column("role")
    private Set<Role> roles;
}
