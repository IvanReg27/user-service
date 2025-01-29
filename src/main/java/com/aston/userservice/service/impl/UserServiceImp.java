package com.aston.userservice.service.impl;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import com.aston.userservice.exception.RequisitesNotFoundException;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import com.aston.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Класс для работы с пользователем
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final RequisitesRepository requisitesRepository;


    /**
     * Метод для получения пользователя по логину
     *
     * @param login login
     * @return пользователь
     */
    @Loggable
    @Override
    public UserProjection findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь не найден по логину: " + login));
    }

    /**
     * Метод для получения реквизитов счета по id пользователя
     *
     * @param userId userId
     * @return реквизиты счета
     */
    @Loggable
    @Override
    public UserRequisitesProjection getUserRequisitesById(UUID userId) {
        return requisitesRepository.findByUserId(userId)
                .orElseThrow(() -> new RequisitesNotFoundException(String.format(
                        "Реквизиты счета не найдены по id пользователя: " + userId.toString())));
    }

    @Loggable
    @Override
    public User save(User user) {
        log.info("Сохранен пользователь с именем {}", user.getFirstName());
        userRepository.save(user);
        log.info("Пользователь сохранен успешно, новый id {}", user.getId());
        return user;
    }
}
