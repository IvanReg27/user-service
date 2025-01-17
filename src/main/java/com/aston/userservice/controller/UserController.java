package com.aston.userservice.controller;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.response.UserRequisitesResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

/**
 * Интерфейс для управления пользователем
 */
@RequestMapping("/user-service")
public interface UserController {

    /**
     * Метод для получения информации о пользователе по login
     *
     * @param login login пользователя
     * @return Информацию о пользователе {@link User}
     */
    @GetMapping("/user/{login}")
    public ResponseEntity<User> getUser(@PathVariable String login);

    /**
     * Метод для получения информации о счете по id пользователя
     *
     * @param id id пользователя
     * @return Информацию о счете {@link UserRequisitesResponseDTO}
     */
    @GetMapping("/requisites/{id}")
    public ResponseEntity<UserRequisitesResponseDTO> getUserRequisites(@PathVariable UUID id);
}
