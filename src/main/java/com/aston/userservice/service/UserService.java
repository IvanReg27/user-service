package com.aston.userservice.service;

import com.aston.userservice.domain.dto.UserDto;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Интерфейс для работы с пользователем
 */
public interface UserService {

    /**
     * Метод для получения пользователя по логину
     *
     * @param login login пользователя
     * @return пользователь
     */
    Mono<UserProjection> findByLogin(String login);

    /**
     * Метод для получения реквизитов счета по id пользователя
     *
     * @param userId userId пользователя
     * @return реквизиты
     */
    Mono<UserRequisitesProjection> getUserRequisitesById(Long userId);

    /**
     * Метод для сохранения пользователя в системе
     *
     * @param userDto пользователь системы
     * @return пользователь
     */
    Mono<String> createUser(UserDto userDto);

    /**
     * Метод для получения списка всех пользователей
     *
     * @return список пользователей
     */
    Flux<UserProjection> getAllUsers();
}
