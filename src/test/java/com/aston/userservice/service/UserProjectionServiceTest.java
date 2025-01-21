package com.aston.userservice.service;

import com.aston.userservice.constants.TestConstantsRequisites;
import com.aston.userservice.constants.TestConstantsUser;
import com.aston.userservice.domain.entity.Requisites;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import com.aston.userservice.integration.cofig.IntegrationTest;
import com.aston.userservice.integration.cofig.PostgresTestContainer;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

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
        User savedUser = userRepository.save(TestConstantsUser.USER);
        UserProjection ivan = userService.findByLogin(TestConstantsUser.LOGIN_NAME);

        Assertions.assertEquals(savedUser.getLogin(), ivan.getLogin());
    }

    @Test
    // @Transactional для загрузки связанной сущности user(по user_id) в одной сессии, иначе ошибка
    @Transactional
    void getUserRequisitesById() {
        User savedUser = userRepository.save(TestConstantsUser.USER);
        Requisites requisites = TestConstantsRequisites.REQUISITES.toBuilder()
                .user(savedUser)
                .build();
        requisites = requisitesRepository.save(requisites);

        String expectedAccountNumber = requisites.getAccountNumber();
        String expectedKbk = requisites.getKbk();
        String expectedFirstName = savedUser.getFirstName();

        UserRequisitesProjection response = userService.getUserRequisitesById(savedUser.getId());

        Assertions.assertAll(
                "Проверка полей проекции UserRequisitesProjection",
                () -> Assertions.assertNotNull(response, "Response не должен быть null"),
                () -> Assertions.assertEquals(expectedAccountNumber, response.getAccountNumber(), "Неверный номер счета"),
                () -> Assertions.assertEquals(expectedKbk, response.getKbk(), "Неверный KBK"),
                () -> Assertions.assertEquals(expectedFirstName, response.getFirstName(), "Неверное имя пользователя")
        );
    }

    @Test
    void getUserRequisitesByIdThrowsExceptionIfNotFound() {
        UUID nonExistentUserId = UUID.randomUUID();

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserRequisitesById(nonExistentUserId)
        );

        Assertions.assertEquals("Реквизиты счета не найдены по id пользователя: "
                + nonExistentUserId, exception.getMessage()
        );
    }
}
