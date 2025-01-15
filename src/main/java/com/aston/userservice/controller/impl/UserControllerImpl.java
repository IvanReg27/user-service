package com.aston.userservice.controller.impl;

import com.aston.userservice.controller.UserController;
import com.aston.userservice.domain.response.UserRequisitesResponseDTO;
import org.springframework.http.ResponseEntity;

public class UserControllerImpl implements UserController {
    @Override
    public ResponseEntity<UserRequisitesResponseDTO> getUserRequisites(Integer id) {
        return ResponseEntity.ok().build();
}
}
