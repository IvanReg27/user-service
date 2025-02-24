package com.aston.userservice.service;

import com.aston.userservice.constants.TestConstantsRequisites;
import com.aston.userservice.constants.TestConstantsUser;
import com.aston.userservice.domain.entity.Requisites;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.exception.RequisitesNotFoundException;
import com.aston.userservice.integration.cofig.IntegrationTest;
import com.aston.userservice.integration.cofig.PostgresTestContainer;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
class UserServiceTest extends PostgresTestContainer {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequisitesRepository requisitesRepository;

    @Test
    void findByLogin() {
        User savedUser = userRepository.save(TestConstantsUser.USER).block();
        assertNotNull(savedUser);

        StepVerifier.create(userService.findByLogin(TestConstantsUser.LOGIN_NAME))
                .expectNextMatches(userProjection -> userProjection.getLogin().equals(savedUser.getLogin()))
                .verifyComplete();
    }

    @Test
    void getUserRequisitesById() {
        User savedUser = userRepository.save(TestConstantsUser.USER).block();
        assertNotNull(savedUser);

        Requisites requisites = TestConstantsRequisites.REQUISITES.toBuilder()
                .userId(savedUser.getId())
                .build();
        requisites = requisitesRepository.save(requisites).block();
        assertNotNull(requisites);

        String expectedAccountNumber = requisites.getAccountNumber();
        String expectedKbk = requisites.getKbk();
        String expectedFirstName = savedUser.getFirstName();

        StepVerifier.create(userService.getUserRequisitesById(savedUser.getId()))
                .assertNext(response -> {
                    assertNotNull(response, "Ответ не должен быть пустым");
                    assertEquals(expectedAccountNumber, response.getAccountNumber(), "Неверный номер счета");
                    assertEquals(expectedKbk, response.getKbk(), "Неверный KБK");
                    assertEquals(expectedFirstName, response.getFirstName(), "Неверное имя пользователя");
                })
                .verifyComplete();
    }

    @Test
    void getUserRequisitesByIdThrowsExceptionIfNotFound() {
        UUID nonExistentUserId = UUID.randomUUID();

        StepVerifier.create(userService.getUserRequisitesById(nonExistentUserId))
                .expectErrorMatches(throwable -> throwable instanceof RequisitesNotFoundException &&
                        throwable.getMessage().equals("Реквизиты счета не найдены по id пользователя: " + nonExistentUserId))
                .verify();
    }
}
