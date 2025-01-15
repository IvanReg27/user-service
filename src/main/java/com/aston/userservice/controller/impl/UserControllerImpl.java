package com.aston.userservice.controller.impl;

import com.aston.userservice.controller.UserController;
import com.aston.userservice.domain.response.UserRequisitesResponseDTO;
import com.aston.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private UserService userService;

    @Override
    public ResponseEntity<UserRequisitesResponseDTO> getUserRequisites(UUID id) {
        return ResponseEntity.ok(userService.getUserRequisitesById(id));
    }
}
