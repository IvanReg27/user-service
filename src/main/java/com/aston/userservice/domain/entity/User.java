package com.aston.userservice.domain.entity;

import com.aston.userservice.security.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс сущность, соответствующий таблице User в UserDB
 */
@Table("users")
@Getter
@Setter
@ToString
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
    private Set<Role> roles = new HashSet<>();

    // Использован Delombok, с целью исключения поля roles
    User(Long id, String firstName, String lastName, LocalDate birthday, String inn, String snils, String passportNumber, String login, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.inn = inn;
        this.snils = snils;
        this.passportNumber = passportNumber;
        this.login = login;
        this.password = password;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UserBuilder toBuilder() {
        return new UserBuilder().id(this.id).firstName(this.firstName).lastName(this.lastName).birthday(this.birthday).inn(this.inn).snils(this.snils).passportNumber(this.passportNumber).login(this.login).password(this.password);
    }

    public static class UserBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private LocalDate birthday;
        private String inn;
        private String snils;
        private String passportNumber;
        private String login;
        private String password;

        UserBuilder() {
        }

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder birthday(LocalDate birthday) {
            this.birthday = birthday;
            return this;
        }

        public UserBuilder inn(String inn) {
            this.inn = inn;
            return this;
        }

        public UserBuilder snils(String snils) {
            this.snils = snils;
            return this;
        }

        public UserBuilder passportNumber(String passportNumber) {
            this.passportNumber = passportNumber;
            return this;
        }

        public UserBuilder login(String login) {
            this.login = login;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public User build() {
            return new User(id, firstName, lastName, birthday, inn, snils, passportNumber, login, password);
        }

        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", firstName=" + this.firstName + ", lastName=" + this.lastName + ", birthday=" + this.birthday + ", inn=" + this.inn + ", snils=" + this.snils + ", passportNumber=" + this.passportNumber + ", login=" + this.login + ", password=" + this.password + ")";
        }
    }
}
