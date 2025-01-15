package com.aston.userservice.controller.impl;

import com.aston.userservice.controller.UserController;
import com.aston.userservice.domain.response.UserRequisitesResponseDTO;
import com.aston.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserControllerImpl implements UserController {

    private UserService userService;

    public UserControllerImpl(UserService userService) {
    }

    @Override
    public ResponseEntity<UserRequisitesResponseDTO> getUserRequisites(UUID id) {
        return ResponseEntity.ok().build();
    }
}
