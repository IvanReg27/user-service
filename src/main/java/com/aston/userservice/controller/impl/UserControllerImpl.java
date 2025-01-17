package com.aston.userservice.controller.impl;

import com.aston.userservice.controller.UserController;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.response.UserRequisitesResponseDTO;
import com.aston.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<User> getUser(@PathVariable String login) {
        return ResponseEntity.ok(userService.findByLogin(login));
    }

    @Override
    public ResponseEntity<UserRequisitesResponseDTO> getUserRequisites(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserRequisitesById(id));
    }
}
