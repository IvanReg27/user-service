package com.aston.userservice.controller.impl;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.controller.UserController;
import com.aston.userservice.domain.dto.UserDto;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import com.aston.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Loggable
    @Override
    public Mono<UserProjection> getUser(@PathVariable String login) {
        return userService.findByLogin(login);
    }

    @Loggable
    @Override
    public Mono<UserRequisitesProjection> getUserRequisites(@PathVariable Long id) {
        return userService.getUserRequisitesById(id);
    }

    @Loggable
    @Override
    public Mono<String> createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @Loggable
    @Override
    public Flux<UserProjection> getAllUsers() {
        return userService.getAllUsers();
    }
}
