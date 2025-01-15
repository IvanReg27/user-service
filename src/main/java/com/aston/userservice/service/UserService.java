package com.aston.userservice.service;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.response.UserRequisitesResponseDTO;

import java.util.UUID;

/**
 * Интерфейс для работы с пользователем
 */
public interface  UserService {

    /**
     * Метод для получения пользователя по логину
     *
     * @param login login пользователя
     * @return пользователь
     */
    User findByLogin(String login);

    /**
     * Метод для получения необходимых реквизитов счета по id пользователя
     *
     * @param id id пользователя
     * @param userRequisitesResponseDTO DTO запроса на получение реквизитов
     * @return реквизиты
     */
    User findRequisitesByIdUser(UUID id, UserRequisitesResponseDTO userRequisitesResponseDTO);
}
