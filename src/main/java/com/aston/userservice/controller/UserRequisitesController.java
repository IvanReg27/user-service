package com.aston.userservice.controller;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.entity.Card;
import com.aston.userservice.domain.entity.UserRequisites;
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

    // Эндпоинт для сохранения документа (реквизиты пользователя)
    @Loggable
    @PostMapping
    public ResponseEntity<UserRequisites> saveUser(@RequestBody UserRequisites userRequisites) {
        UserRequisites savedUser = userRequisitesService.saveUser(userRequisites);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    // Эндпоинт для обновления документа (реквизиты пользователя). С возможностью добавления карт и счетов
    @Loggable
    @PutMapping("/{id}")
    public ResponseEntity<UserRequisites> updateUser(@PathVariable String id, @RequestBody UserRequisites updateRequest) {
        UserRequisites updatedUser = userRequisitesService.updateUserRequisites(id, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }
    // Эндпоинт для получения документа (реквизиты пользователя) по userId
    @Loggable
    @GetMapping("/{userId}")
    public ResponseEntity<UserRequisites> getUserById(@PathVariable String userId) {
        UserRequisites user = userRequisitesService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    // Эндпоинт для получения карт пользователя по его userId. Установлен фильтр на карты
    // (срок действия которых истекает менее чем через 30 дней) или получаем весь список карт
    @Loggable
    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<Card>> getUserCards(@PathVariable String userId,
                                                   @RequestParam(value = "expiringSoon", required = false, defaultValue = "false") boolean expiringSoon) {
        List<Card> cards = userRequisitesService.getUserCards(userId, expiringSoon);
        return ResponseEntity.ok(cards);
    }
}
