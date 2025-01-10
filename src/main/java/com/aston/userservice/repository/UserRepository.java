package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс репозитория для работы с пользователем
 *
 * @see User
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Метод для получения пользователя по login
     *
     * @param login login карты
     * @return пользователь с заданным login
     */
    Optional<User> findByLogin(String login);
}
