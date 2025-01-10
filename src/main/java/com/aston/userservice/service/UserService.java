package com.aston.userservice.service;

import com.aston.userservice.domain.entity.User;

/**
 * Интерфейс для работы с с пользователем
 */
public interface  UserService {

    /**
     * Метод для получения пользователя по логину
     *
     * @param login login пользователя
     * @return пользователь
     */
    User findByLogin(String login);
}
