package com.aston.userservice.service;

import com.aston.userservice.domain.dto.RequisitesResponseDto;
import com.aston.userservice.domain.dto.UserResponseDto;
import com.aston.userservice.domain.projection.UserProjection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    Mono<RequisitesResponseDto> getUserRequisitesById(Long userId);

    /**
     * Метод для сохранения пользователя в системе
     *
     * @param userResponseDto пользователь системы
     * @return пользователь
     */
    Mono<String> createUser(UserResponseDto userResponseDto);

    /**
     * Метод для получения списка всех пользователей
     *
     * @return список пользователей
     */
    Flux<UserProjection> getAllUsers();
}
