package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.projection.UserProjection;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Интерфейс репозитория для работы с пользователем
 *
 * @see UserProjection
 */
@Repository
public interface UserRepository extends R2dbcRepository<User, UUID> {

    /**
     * Метод для получения пользователя по login
     *
     * @param login login пользователя
     * @return пользователь с заданным login
     */
    Mono<UserProjection> findByLogin(String login);

    /**
     * Метод для получения пользователя по Inn
     *
     * @param inn inn пользователя
     * @return пользователь с заданным inn
     */
    Mono<User> findByInn(String inn);

    /**
     * Метод для получения списка всех пользователей
     *
     * @return список пользователей
     */
    Flux<UserProjection> findAllUsersBy();
}
