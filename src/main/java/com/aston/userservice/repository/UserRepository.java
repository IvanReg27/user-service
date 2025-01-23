package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс репозитория для работы с пользователем
 *
 * @see UserProjection
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Метод для получения пользователя по login
     *
     * @param login login пользователя
     * @return пользователь с заданным login
     */
    Optional<UserProjection> findByLogin(String login);
}
