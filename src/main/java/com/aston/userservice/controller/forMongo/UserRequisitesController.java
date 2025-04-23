package com.aston.userservice.controller.forMongo;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.entity.forMongo.Card;
import com.aston.userservice.domain.entity.forMongo.UserRequisites;
import com.aston.userservice.service.forMongo.UserRequisitesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mongo")
@RequiredArgsConstructor
@Slf4j
public class UserRequisitesController {

    private final UserRequisitesService userRequisitesService;

    // Эндпоинт для сохранения документа (реквизиты пользователя)
    @Loggable
    @PostMapping("/user")
    public UserRequisites createUser(@RequestBody UserRequisites userRequisites) {
        return userRequisitesService.createUser(userRequisites);
    }
    // Эндпоинт для обновления документа (реквизиты пользователя). С возможностью добавления карт и счетов
    @Loggable
    @PutMapping("/user/{id}")
    public UserRequisites updateUser(@PathVariable String id, @RequestBody UserRequisites updateRequest) {
        return userRequisitesService.updateUserRequisites(id, updateRequest);
    }
    // Эндпоинт для получения документа (реквизиты пользователя) по userId
    @Loggable
    @GetMapping("/user/{userId}")
    public UserRequisites getUserById(@PathVariable String userId) {
        return userRequisitesService.getUserById(userId);
    }
    // Эндпоинт для получения карт пользователя по его userId. Установлен фильтр на карты
    // (срок действия которых истекает менее чем через 30 дней) или получаем весь список карт
    @Loggable
    @GetMapping("/user/{userId}/cards")
    public List<Card> getUserCards (@PathVariable String userId,
                                    @RequestParam(value = "expiringSoon", required = false, defaultValue = "false")
                                    boolean expiringSoon) {
        return userRequisitesService.getUserCards(userId, expiringSoon);
    }
}
