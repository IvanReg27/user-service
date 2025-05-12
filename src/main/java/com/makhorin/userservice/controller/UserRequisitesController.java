package com.aston.userservice.controller;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.dto.CardDto;
import com.aston.userservice.domain.dto.UserRequisitesDto;
import com.aston.userservice.service.UserRequisitesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Контроллер для работы с MongoDB
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserRequisitesController {

    private final UserRequisitesService userRequisitesService;

    // Сохранение нового документа
    @Loggable
    @PostMapping
    public ResponseEntity<UserRequisitesDto> saveUser(@RequestBody UserRequisitesDto userRequisitesDto) {
        UserRequisitesDto savedUser = userRequisitesService.saveUser(userRequisitesDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Обновление документа
    @Loggable
    @PutMapping("/{Id}")
    public ResponseEntity<UserRequisitesDto> updateUser(@PathVariable String Id,
                                                        @RequestBody UserRequisitesDto updateRequest) {
        UserRequisitesDto updatedUser = userRequisitesService.updateUserRequisites(Id, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    // Получение документа по userId
    @Loggable
    @GetMapping("/{userId}")
    public ResponseEntity<UserRequisitesDto> getUserById(@PathVariable String userId) {
        UserRequisitesDto user = userRequisitesService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // Получение списка карт документа
    @Loggable
    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<CardDto>> getUserCards(
            @PathVariable String userId,
            @RequestParam(value = "expiringSoon", required = false, defaultValue = "false") boolean expiringSoon) {
        List<CardDto> cards = userRequisitesService.getUserCards(userId, expiringSoon);
        return ResponseEntity.ok(cards);
    }
}
