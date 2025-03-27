package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Метод для получения пользователя по Inn
     *
     * @param inn inn пользователя
     * @return пользователь с заданным inn
     */
    Optional<User> findByInn(String inn);

    /**
     * Метод для получения списка всех пользователей
     *
     * @return список всех пользователей
     */
    List<UserProjection> findAllBy();
}
