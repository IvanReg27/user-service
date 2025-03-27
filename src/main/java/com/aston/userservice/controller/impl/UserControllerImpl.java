package com.aston.userservice.controller.impl;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.controller.UserController;
import com.aston.userservice.domain.dto.UserDto;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import com.aston.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Loggable
    @Override
    public ResponseEntity<UserProjection> getUser(@PathVariable String login) {
        return ResponseEntity.ok(userService.findByLogin(login));
    }

    @Loggable
    @Override
    public ResponseEntity<UserRequisitesProjection> getUserRequisites(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserRequisitesById(id));
    }

    @Loggable
    @Override
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        String userId = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @Loggable
    @Override
    public ResponseEntity<List<UserProjection>> getUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }
}
