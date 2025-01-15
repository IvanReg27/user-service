package com.aston.userservice.controller;

import com.aston.userservice.domain.response.UserRequisitesResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user-service/users")
public interface UserController {
    public ResponseEntity<UserRequisitesResponseDTO> getUserRequisites(@PathVariable Integer id);
}
