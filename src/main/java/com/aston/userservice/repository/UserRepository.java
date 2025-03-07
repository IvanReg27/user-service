package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.projection.UserProjection;
import org.springframework.data.r2dbc.repository.Query;
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
    public interface UserRepository extends R2dbcRepository<User, Long> {

    /**
     * Метод для получения пользователя по login
     *
     * @param login login пользователя
     * @return пользователь с заданным login
     */
    @Query("SELECT u.first_name, u.last_name, u.birthday, u.inn, u.snils, u.passport_number, u.login, " +
                    "u.password, r.role FROM users u INNER JOIN user_roles r ON u.id = r.user_id WHERE " +
                    "u.login = :login")
    Mono<UserProjection> findByLogin(String login);

    /**
     * Метод для получения пользователя по Inn
     *
     * @param inn inn пользователя
     * @return пользователь с заданным inn
     */
    @Query("SELECT * FROM users WHERE inn = :inn")
    Mono<User> findByInn(String inn);

    /**
     * Метод для получения списка всех пользователей
     *
     * @return список пользователей
     */
    @Query(
            "SELECT u.first_name, u.last_name, u.birthday, u.inn, u.snils, u.passport_number, u.login, " +
                    "u.password, r.role FROM users u INNER JOIN user_roles r ON u.id = r.user_id")
    Flux<UserProjection> findAllUsersBy();
}
