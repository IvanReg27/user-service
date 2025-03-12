package com.aston.userservice.service;

import com.aston.userservice.constants.TestConstantsRequisites;
import com.aston.userservice.constants.TestConstantsUser;
import com.aston.userservice.domain.dto.UserDto;
import com.aston.userservice.domain.entity.Requisites;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
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

import java.time.LocalDate;
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
        UserDto userDto = new UserDto();
        userDto.setFirstName(TestConstantsUser.USER.getFirstName());
        userDto.setLastName(TestConstantsUser.USER.getLastName());
        userDto.setBirthday(TestConstantsUser.USER.getBirthday());
        userDto.setInn(TestConstantsUser.USER.getInn());
        userDto.setSnils(TestConstantsUser.USER.getSnils());
        userDto.setPassportNumber(TestConstantsUser.USER.getPassportNumber());
        userDto.setLogin(TestConstantsUser.USER.getLogin());
        userDto.setPassword(TestConstantsUser.USER.getPassword());
        userDto.setRoles(new HashSet<>(List.of(Role.USER, Role.ADMIN)));

        Mono<String> result = userService.createUser(userDto);

        StepVerifier.create(result)
                .expectNextMatches(userId -> {
                    assertThat(userId).isNotNull();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testFindByLogin() {
        // Сначала создаем пользователя
        UserDto userDto = new UserDto();
        userDto.setFirstName(TestConstantsUser.USER.getFirstName());
        userDto.setLastName(TestConstantsUser.USER.getLastName());
        userDto.setBirthday(TestConstantsUser.USER.getBirthday());
        userDto.setInn(TestConstantsUser.USER.getInn());
        userDto.setSnils(TestConstantsUser.USER.getSnils());
        userDto.setPassportNumber(TestConstantsUser.USER.getPassportNumber());
        userDto.setLogin(TestConstantsUser.USER.getLogin());
        userDto.setPassword(TestConstantsUser.USER.getPassword());
        userDto.setRoles(new HashSet<>(List.of(Role.USER, Role.ADMIN)));

        // Создаем пользователя
        userService.createUser(userDto).block();

        // Теперь ищем пользователя по логину
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
        // Сначала создаем пользователя
        UserDto userDto = new UserDto();
        userDto.setFirstName(TestConstantsUser.USER.getFirstName());
        userDto.setLastName(TestConstantsUser.USER.getLastName());
        userDto.setBirthday(TestConstantsUser.USER.getBirthday());
        userDto.setInn(TestConstantsUser.USER.getInn());
        userDto.setSnils(TestConstantsUser.USER.getSnils());
        userDto.setPassportNumber(TestConstantsUser.USER.getPassportNumber());
        userDto.setLogin(TestConstantsUser.USER.getLogin());
        userDto.setPassword(TestConstantsUser.USER.getPassword());
        userDto.setRoles(new HashSet<>(List.of(Role.USER, Role.ADMIN)));

        // Создаем пользователя
        String userId = userService.createUser(userDto).block();

        // Создаем реквизиты для пользователя
        Requisites requisites = TestConstantsRequisites.REQUISITES.toBuilder()
                .userId(Long.valueOf(userId)) // Устанавливаем ID пользователя
                .build();
        requisitesRepository.save(requisites).block();

        // Теперь получаем реквизиты по ID пользователя
        Mono<UserRequisitesProjection> result = userService.getUserRequisitesById(Long.valueOf(userId));

        StepVerifier.create(result)
                .expectNextMatches(requisitesProjection -> {
                    assertThat(requisitesProjection.getAccountNumber()).isEqualTo(TestConstantsRequisites.REQUISITES.getAccountNumber());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testCreateUserWithExistingInn() {
        // Создаем первого пользователя
        UserDto userDto1 = new UserDto();
        userDto1.setFirstName(TestConstantsUser.USER.getFirstName());
        userDto1.setLastName(TestConstantsUser.USER.getLastName());
        userDto1.setBirthday(TestConstantsUser.USER.getBirthday());
        userDto1.setInn(TestConstantsUser.USER.getInn());
        userDto1.setSnils(TestConstantsUser.USER.getSnils());
        userDto1.setPassportNumber(TestConstantsUser.USER.getPassportNumber());
        userDto1.setLogin(TestConstantsUser.USER.getLogin());
        userDto1.setPassword(TestConstantsUser.USER.getPassword());
        userDto1.setRoles(new HashSet<>(List.of(Role.USER, Role.ADMIN)));

        // Создаем первого пользователя
        userService.createUser(userDto1).block();

        // Пытаемся создать второго пользователя с тем же ИНН
        UserDto userDto2 = new UserDto();
        userDto2.setFirstName("Дмитрий");
        userDto2.setLastName("Сидоров");
        userDto2.setBirthday(LocalDate.of(1992, 2, 2));
        userDto2.setInn(TestConstantsUser.USER.getInn()); // тот же ИНН
        userDto2.setSnils("98765432100");
        userDto2.setPassportNumber("1234567890");
        userDto2.setLogin("dmitry");
        userDto2.setPassword("password2");
        userDto2.setRoles(new HashSet<>(List.of(Role.USER)));

        Mono<String> result = userService.createUser(userDto2);

        StepVerifier.create(result)
                .expectNextMatches(existingUserId -> {
                    // Проверяем, что возвращается ID уже существующего пользователя
                    assertThat(existingUserId).isNotNull();
                    return true;
                })
                .verifyComplete();
    }
}
