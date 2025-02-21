package com.aston.userservice.controller;

import com.aston.userservice.domain.dto.UserDto;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс для управления пользователем
 *
 */
@RequestMapping("/user-service")
public interface UserController {

    /**
     * Метод для получения информации о пользователе по login
     *
     * @param login login пользователя
     * @return Информацию о пользователе {@link UserProjection}
     */
    @GetMapping("/user/{login}")
    public ResponseEntity<UserProjection> getUser(@PathVariable String login);

    /**
     * Метод для получения информации о счете по id пользователя
     *
     * @param id id пользователя
     * @return Информацию о счете {@link UserRequisitesProjection}
     */
    @GetMapping("/requisites/{id}")
    public ResponseEntity<UserRequisitesProjection> getUserRequisites(@PathVariable UUID id);

    /**
     * Метод для сохранения нового пользователя в систему
     *
     * @param userDto пользователь в системе {@link User}
     */
    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto);

    /**
     * Метод для получения списка всех пользователей
     *
     * @return Список пользователей {@link UserProjection}
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserProjection>> getAllUsers();
}
