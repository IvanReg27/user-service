package com.aston.userservice.domain.entity;

import com.aston.userservice.security.Role;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Класс сущность, соответствующий таблице User в UserDB
 */
@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class User {

    @Id
    private UUID id;

    private String firstName;

    private String lastName;

    private LocalDate birthday;

    private String inn;

    private String snils;

    private String passportNumber;

    private String login;

    private String password;

    @Column("role")
    private Set<Role> roles;
}
