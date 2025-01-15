package com.aston.userservice.controller;

import com.aston.userservice.domain.response.UserRequisitesResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/user-service/users")
public interface UserController {

    @GetMapping("/{id}/requisites")
    public ResponseEntity<UserRequisitesResponseDTO> getUserRequisites(@PathVariable UUID id);
}
