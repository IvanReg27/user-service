package ru.makhorin.userservice.service.impl;

import ru.makhorin.userservice.domain.dto.UserDto;
import ru.makhorin.userservice.domain.entity.User;
import ru.makhorin.userservice.domain.projection.UserProjection;
import ru.makhorin.userservice.domain.projection.UserRequisitesProjection;
import ru.makhorin.userservice.exception.RequisitesNotFoundException;
import ru.makhorin.userservice.exception.UserNotFoundException;
import ru.makhorin.userservice.repository.RequisitesRepository;
import ru.makhorin.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RequisitesRepository requisitesRepository;
    @Mock
    private KafkaProducerServiceImpl kafkaProducerServiceImpl;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserProjection userProjection;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private UserDto newUserDto;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setLogin("Boris");
        user.setPassword("password123");
        user.setInn("783456789088");

        userDto = new UserDto();
        userDto.setLogin("Boris");
        userDto.setPassword("password123");
        userDto.setInn("783456789088");

        newUserDto = new UserDto();
        newUserDto.setLogin("NewBoris");
        newUserDto.setPassword("password456");
        newUserDto.setInn("783456789099");
    }

    // Позитивный сценарий
    @Test
    void findByLogin_UserExistsTest() {
        when(userRepository.findByLogin("Boris")).thenReturn(Optional.of(userProjection));
        when(userProjection.getLogin()).thenReturn("Boris");

        UserProjection result = userService.findByLogin("Boris");

        assertNotNull(result);
        assertEquals("Boris", result.getLogin());
    }

    // Негативный сценарий
    @Test
    void findByLogin_UserNotFoundTest() {
        when(userRepository.findByLogin("unknownBoris")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByLogin("unknownBoris"));
    }

    // Позитивный сценарий
    @Test
    void getUserRequisitesById_UserHasRequisitesTest() {
        UserRequisitesProjection requisites = mock(UserRequisitesProjection.class);
        when(requisitesRepository.findByUserId(userId)).thenReturn(Optional.of(requisites));
        UserRequisitesProjection result = userService.getUserRequisitesById(userId);

        assertNotNull(result);
    }

    // Негативный сценарий
    @Test
    void getUserRequisitesById_RequisitesNotFoundTest() {
        when(requisitesRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(RequisitesNotFoundException.class, () -> userService.getUserRequisitesById(userId));
    }

    // Позитивный сценарий
    @Test
    void createUser_NewUserTest() {
        when(userRepository.findByInn("783456789088")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("Зашифрованный пароль");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userService.createUser(userDto);

        assertEquals(userId.toString(), result);

        verify(kafkaProducerServiceImpl).sendUserCreatedEvent(any(User.class));
    }

    // Негативный сценарий
    @Test
    void createUser_UserAlreadyExistsTest() {
        when(userRepository.findByInn("783456789088")).thenReturn(Optional.of(user));

        String result = userService.createUser(userDto);

        assertEquals(userId.toString(), result);

        verify(userRepository, never()).save(any(User.class));
    }

    // Позитивный сценарий
    @Test
    void findAllUsers_UsersExistTest() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDto> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());

        UserDto resultDto = result.get(0);

        assertEquals(user.getLogin(), resultDto.getLogin());
        assertEquals(user.getPassword(), resultDto.getPassword());
        assertEquals(user.getInn(), resultDto.getInn());
    }

    // Негативный сценарий
    @Test
    void findAllUsers_UsersNotExistTest() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(UserNotFoundException.class, () -> userService.findAllUsers());
    }

    // Позитивный сценарий
    @Test
    void deleteByLogin_UserExistsTest() {
        String login = "Boris";

        when(userRepository.existsByLogin(login)).thenReturn(true);

        userService.deleteByLogin(login);

        verify(userRepository, times(1)).deleteByLogin(login);
    }

    // Негативный сценарий
    @Test
    void deleteByLogin_UserNotFoundTest() {
        String login = "nonBoris";

        when(userRepository.existsByLogin(login)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteByLogin(login));

        verify(userRepository, never()).deleteByLogin(anyString());
    }

    // Позитивный сценарий
    @Test
    void updateUser_UserExistsTest() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode("password456")).thenReturn("password456");

        String result = userService.updateUser(userId, newUserDto);

        assertEquals(userId.toString(), result);
        assertEquals("NewBoris", user.getLogin());
        assertEquals("password456", user.getPassword());
        assertEquals("783456789099", user.getInn());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_UserNotFoundTest() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, newUserDto);
        }, "Пользователь с id " + userId + " не найден");

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    // Позитивный сценарий
    @Test
    void loadUserByUsername_UserExistsTest() {
        when(userRepository.findByLogin("Boris")).thenReturn(Optional.of(userProjection));
        when(userProjection.getLogin()).thenReturn("Boris");
        when(userProjection.getPassword()).thenReturn("Зашифрованный пароль");
        when(userProjection.getRoles()).thenReturn(Collections.emptySet());

        UserDetails result = userService.loadUserByUsername("Boris");

        assertNotNull(result);
        assertEquals("Boris", result.getUsername());
    }

    // Негативный сценарий
    @Test
    void loadUserByUsername_UserNotFoundTest() {
        when(userRepository.findByLogin("unknownBoris")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.loadUserByUsername("unknownBoris"));
    }
}
