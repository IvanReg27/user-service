package com.aston.userservice.service;

import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;

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
    UserProjection findByLogin(String login);

    /**
     * Метод для получения реквизитов счета по id пользователя
     *
     * @param userId userId пользователя
     * @return реквизиты
     */
    UserRequisitesProjection getUserRequisitesById(UUID userId);
}
