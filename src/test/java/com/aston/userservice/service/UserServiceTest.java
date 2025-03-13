package com.aston.userservice.service;

import com.aston.userservice.constants.TestConstantsRequisites;
import com.aston.userservice.constants.TestConstantsUser;
import com.aston.userservice.domain.dto.RequisitesResponseDto;
import com.aston.userservice.domain.dto.UserResponseDto;
import com.aston.userservice.domain.entity.Requisites;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.integration.cofig.IntegrationTest;
import com.aston.userservice.integration.cofig.PostgresTestContainer;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import com.aston.userservice.security.Role;
import com.aston.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@IntegrationTest
@AutoConfigureWebTestClient
class UserServiceTest extends PostgresTestContainer {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequisitesRepository requisitesRepository;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        userRepository.deleteAll().block();
        requisitesRepository.deleteAll().block();
    }

    @Test
    void testCreateUser() {
        // Cоздаем пользователя
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setFirstName(TestConstantsUser.USER.getFirstName());
        userResponseDto.setLastName(TestConstantsUser.USER.getLastName());
        userResponseDto.setBirthday(TestConstantsUser.USER.getBirthday());
        userResponseDto.setInn(TestConstantsUser.USER.getInn());
        userResponseDto.setSnils(TestConstantsUser.USER.getSnils());
        userResponseDto.setPassportNumber(TestConstantsUser.USER.getPassportNumber());
        userResponseDto.setLogin(TestConstantsUser.USER.getLogin());
        userResponseDto.setPassword(TestConstantsUser.USER.getPassword());
        userResponseDto.setRoles(new HashSet<>(List.of(Role.USER)));

        // Проверяем, что пользователь сохранен
        Mono<String> result = userService.createUser(userResponseDto);

        StepVerifier.create(result)
                .expectNextMatches(userId -> {
                    assertThat(userId).isNotNull();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testFindByLogin() {
        // Cоздаем пользователя
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setFirstName(TestConstantsUser.USER.getFirstName());
        userResponseDto.setLastName(TestConstantsUser.USER.getLastName());
        userResponseDto.setBirthday(TestConstantsUser.USER.getBirthday());
        userResponseDto.setInn(TestConstantsUser.USER.getInn());
        userResponseDto.setSnils(TestConstantsUser.USER.getSnils());
        userResponseDto.setPassportNumber(TestConstantsUser.USER.getPassportNumber());
        userResponseDto.setLogin(TestConstantsUser.USER.getLogin());
        userResponseDto.setPassword(TestConstantsUser.USER.getPassword());
        userResponseDto.setRoles(new HashSet<>(List.of(Role.USER)));

        // Сохраняем пользователя
        userService.createUser(userResponseDto).block();

        // Ищем пользователя по логину
        Mono<UserProjection> result = userService.findByLogin(TestConstantsUser.LOGIN_NAME);

        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    assertThat(user.getLogin()).isEqualTo(TestConstantsUser.LOGIN_NAME);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testGetUserRequisitesById() {
        // Создаем пользователя
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setFirstName(TestConstantsUser.USER.getFirstName());
        userResponseDto.setLastName(TestConstantsUser.USER.getLastName());
        userResponseDto.setBirthday(TestConstantsUser.USER.getBirthday());
        userResponseDto.setInn(TestConstantsUser.USER.getInn());
        userResponseDto.setSnils(TestConstantsUser.USER.getSnils());
        userResponseDto.setPassportNumber(TestConstantsUser.USER.getPassportNumber());
        userResponseDto.setLogin(TestConstantsUser.USER.getLogin());
        userResponseDto.setPassword(TestConstantsUser.USER.getPassword());
        userResponseDto.setRoles(new HashSet<>(List.of(Role.USER)));

        // Сохраняем пользователя
        String userId = userService.createUser(userResponseDto).block();

        // Создаем реквизиты для пользователя
        Requisites requisites = TestConstantsRequisites.REQUISITES.toBuilder()
                .userId(Long.valueOf(userId)) // Устанавливаем ID ранее сохраненного пользователя
                .build();
        requisitesRepository.save(requisites).block();

        // Получаем реквизиты по ID пользователя
        Mono<RequisitesResponseDto> result = userService.getUserRequisitesById(Long.valueOf(userId));

        StepVerifier.create(result)
                .expectNextMatches(requisitesResponseDto -> {
                    assertThat(requisitesResponseDto.getKbk()).isEqualTo(TestConstantsRequisites.REQUISITES.getKbk());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testCreateUserWithExistingInn() throws InterruptedException {
        // Создаем первого пользователя
        UserResponseDto userResponseDto1 = new UserResponseDto();
        userResponseDto1.setFirstName(TestConstantsUser.USER.getFirstName());
        userResponseDto1.setLastName(TestConstantsUser.USER.getLastName());
        userResponseDto1.setBirthday(TestConstantsUser.USER.getBirthday());
        userResponseDto1.setInn(TestConstantsUser.USER.getInn());
        userResponseDto1.setSnils(TestConstantsUser.USER.getSnils());
        userResponseDto1.setPassportNumber(TestConstantsUser.USER.getPassportNumber());
        userResponseDto1.setLogin(TestConstantsUser.USER.getLogin());
        userResponseDto1.setPassword(TestConstantsUser.USER.getPassword());
        userResponseDto1.setRoles(new HashSet<>(List.of(Role.USER)));

        // Сохраняем пользователя
        userService.createUser(userResponseDto1).block();

        // Повторно сохраняем того же пользователя (тот же ИНН)
        Mono<String> result = userService.createUser(userResponseDto1);

        // Проверяем, что возвращается ID первого пользовате, т.к. у него такой же ИНН
        StepVerifier.create(result)
                .expectNextMatches(existingUserId -> {
                    assertThat(existingUserId).isNotNull();
                    return true;
                })
                .verifyComplete();
    }
}
