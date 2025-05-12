package ru.makhorin.userservice.service;

import ru.makhorin.userservice.constants.TestConstantsRequisites;
import ru.makhorin.userservice.constants.TestConstantsUser;
import ru.makhorin.userservice.domain.dto.UserDto;
import ru.makhorin.userservice.domain.entity.Requisites;
import ru.makhorin.userservice.domain.entity.User;
import ru.makhorin.userservice.domain.projection.UserProjection;
import ru.makhorin.userservice.domain.projection.UserRequisitesProjection;
import ru.makhorin.userservice.exception.RequisitesNotFoundException;
import ru.makhorin.userservice.exception.UserNotFoundException;
import ru.makhorin.userservice.integration.cofig.IntegrationTest;
import ru.makhorin.userservice.integration.cofig.PostgresTestContainer;
import ru.makhorin.userservice.repository.RequisitesRepository;
import ru.makhorin.userservice.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest extends PostgresTestContainer {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequisitesRepository requisitesRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("users").clear();
    }

    @Test
    @Order(2)
    void findByLoginTest() {
        User savedUser = userRepository.save(TestConstantsUser.USER);
        UserProjection ivan = userService.findByLogin(TestConstantsUser.LOGIN_NAME);

        assertEquals(savedUser.getLogin(), ivan.getLogin());
    }

    // Позитивный сценарий
    @Test
    @Order(3)
    void getUserRequisitesByIdTest() {
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
                () -> Assertions.assertNotNull(response, "Ответ не должен быть null"),
                () -> assertEquals(expectedAccountNumber, response.getAccountNumber(), "Неверный номер счета"),
                () -> assertEquals(expectedKbk, response.getKbk(), "Неверный KBK"),
                () -> assertEquals(expectedFirstName, response.getFirstName(), "Неверное имя пользователя")
        );
    }

    // Негативный сценарий
    @Test
    @Order(4)
    void getUserRequisitesByIdNotFoundTest() {
        UUID nonExistentUserId = UUID.randomUUID();

        RequisitesNotFoundException exception = assertThrows(
                RequisitesNotFoundException.class,
                () -> userService.getUserRequisitesById(nonExistentUserId)
        );

        assertEquals("Реквизиты счета не найдены по id пользователя: "
                + nonExistentUserId, exception.getMessage()
        );
    }

    // Позитивный сценарий
    @Test
    @Order(5)
    void findAllUsersTest() {
        User savedUser = userRepository.save(TestConstantsUser.USER);

        List<UserDto> result = userService.findAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        UserDto userDto = result.get(0);
        assertEquals(TestConstantsUser.LOGIN_NAME, userDto.getLogin());
        assertEquals("Иван", userDto.getFirstName());
        assertEquals("Петров", userDto.getLastName());
        assertEquals("789856132312", userDto.getInn());
    }

    // Негативный сценарий
    @Test
    @Order(1)
    void findAllUsersNotFoundTest() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.findAllUsers();
        });

        assertEquals("Пользователи отсутствуют в базе данных", exception.getMessage());
    }

    // Позитивный сценарий
    @Test
    @Order(8)
    void deleteByLoginTest() {
        userRepository.save(TestConstantsUser.USER);

        userService.deleteByLogin(TestConstantsUser.LOGIN_NAME);

        boolean exists = userRepository.existsByLogin(TestConstantsUser.LOGIN_NAME);

        assertFalse(exists);
    }

    // Негативный сценарий
    @Test
    @Order(9)
    void deleteByLoginNotFoundTest() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteByLogin("sergey");
        });

        assertEquals("Пользователь не найден по логину: sergey", exception.getMessage());
    }

    // Позитивный сценарий
    @Test
    @Order(6)
    void updateUserTest() {
        User savedUser = userRepository.save(TestConstantsUser.USER);
        UUID userId = savedUser.getId();
        UserDto updateDto = UserDto.builder()
                .firstName("Алексей")
                .lastName("Иванов")
                .login("alex")
                .password("0708567891")
                .inn("789856132313")
                .build();

        String resultId = userService.updateUser(userId, updateDto);

        assertEquals(userId.toString(), resultId);
        User updatedUser = userRepository.findById(userId).orElseThrow();
        assertEquals("Алексей", updatedUser.getFirstName());
        assertEquals("Иванов", updatedUser.getLastName());
        assertEquals("alex", updatedUser.getLogin());
        assertEquals("789856132313", updatedUser.getInn());
        assertNotEquals("0708567891", updatedUser.getPassword()); // После PasswordEncoder поэтому пароль будет не равен
    }

    // Негативный сценарий
    @Test
    @Order(7)
    void updateUserNotFoundTest() {
        UUID nonExistentId = UUID.randomUUID();
        UserDto updateDto = UserDto.builder()
                .firstName("Алексей")
                .build();

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(nonExistentId, updateDto);
        });
        assertEquals("Пользователь с id " + nonExistentId + " не найден", exception.getMessage());
    }
}
