package com.aston.userservice.domain.projection;

import com.aston.userservice.security.Role;

import java.time.LocalDate;
import java.util.Set;

/**
 * Проекция для извлечения сведений о пользователе.
 */
public interface UserProjection {

    String getFirstName();

    String getLastName();

    LocalDate getBirthday();

    String getInn();

    String getSnils();

    String getPassportNumber();

    String getLogin();

    String getPassword();

    Set<Role> getRoles();
}
