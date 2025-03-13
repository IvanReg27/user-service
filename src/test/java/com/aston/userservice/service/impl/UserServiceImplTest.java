package com.aston.userservice.service.impl;

import com.aston.userservice.domain.dto.RequisitesResponseDto;
import com.aston.userservice.domain.dto.UserResponseDto;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import com.aston.userservice.exception.RequisitesNotFoundException;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import com.aston.userservice.repository.UserRoleRepository;
import com.aston.userservice.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDto userResponseDto;
    private Long userId;

    @BeforeEach
    void setUp() {
        userResponseDto = new UserResponseDto();
        userResponseDto.setLogin("Boris");
        userResponseDto.setPassword("password123");
        userResponseDto.setInn("783456789088");

        userId = 1L;
        Role userRole = Role.USER;
        userResponseDto.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        user = User.builder()
                .id(userId)
                .firstName("Boris")
                .lastName("Ivanov")
                .birthday(LocalDate.of(1980, 1, 1))
                .inn("783456789088")
                .snils("123-456-789 01")
                .passportNumber("AB1234567")
                .login("Boris")
                .password("Зашифрованный пароль")
                .build();

        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
    }

    // Позитивный сценарий
    @Test
    void findByLogin_UserExistsTest() {
        when(userRepository.findByLogin("Boris")).thenReturn(Mono.just(userProjection));
        when(userProjection.getLogin()).thenReturn("Boris");

        StepVerifier.create(userService.findByLogin("Boris"))
                .expectNextMatches(projection -> projection.getLogin().equals("Boris"))
                .verifyComplete();
    }

    // Негативный сценарий
    @Test
    void findByLogin_UserNotFoundTest() {
        when(userRepository.findByLogin("unknownBoris")).thenReturn(Mono.empty());

        StepVerifier.create(userService.findByLogin("unknownBoris"))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    // Позитивный сценарий
    @Test
    void getUserRequisitesById_UserHasRequisitesTest() {
        RequisitesResponseDto requisites = mock(RequisitesResponseDto.class);
        when(requisitesRepository.findByUserId(userId)).thenReturn(Mono.just(requisites));

        StepVerifier.create(userService.getUserRequisitesById(userId))
                .expectNext(requisites)
                .verifyComplete();
    }

    // Негативный сценарий
    @Test
    void getUserRequisitesById_RequisitesNotFoundTest() {
        when(requisitesRepository.findByUserId(userId)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUserRequisitesById(userId))
                .expectError(RequisitesNotFoundException.class)
                .verify();
    }

    // Позитивный сценарий
    @Test
    void createUser_NewUserTest() {
        // Мокаем поведение репозиториев
        when(userRepository.findByInn("783456789088")).thenReturn(Mono.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("Зашифрованный пароль");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(userRoleRepository.saveAll(anyIterable())).thenReturn(Flux.empty());

        StepVerifier.create(userService.createUser(userResponseDto))
                .expectNext(userId.toString())
                .verifyComplete();

        verify(kafkaProducerServiceImpl).sendUserCreatedEvent(any(User.class));
    }

    // Негативный сценарий
    @Test
    void createUser_UserAlreadyExistsTest() {
        when(userRepository.findByInn("783456789088")).thenReturn(Mono.just(user));

        StepVerifier.create(userService.createUser(userResponseDto))
                .expectNext(userId.toString())
                .verifyComplete();

        verify(userRepository, never()).save(any(User.class));
    }

    // Позитивный сценарий
    @Test
    void getAllUsers_UserExistsTest() {
        UserProjection userProjection1 = mock(UserProjection.class);
        UserProjection userProjection2 = mock(UserProjection.class);

        when(userRepository.findAllUsersBy()).thenReturn(Flux.just(userProjection1, userProjection2));

        StepVerifier.create(userService.getAllUsers())
                .expectNext(userProjection1, userProjection2)
                .verifyComplete();
    }

    // Негативный сценарий
    @Test
    void getAllUsers_UsersNotFoundTest() {
        when(userRepository.findAllUsersBy()).thenReturn(Flux.empty());

        StepVerifier.create(userService.getAllUsers())
                .expectNextCount(0)
                .verifyComplete();
    }

    // Позитивный сценарий
    @Test
    void loadUserByUsername_UserExistsTest() {
        when(userRepository.findByLogin("Boris")).thenReturn(Mono.just(userProjection));
        when(userProjection.getLogin()).thenReturn("Boris");
        when(userProjection.getPassword()).thenReturn("Зашифрованный пароль");
        when(userProjection.getRoles()).thenReturn(Collections.emptySet());

        StepVerifier.create(userService.findByUsername("Boris"))
                .expectNextMatches(userDetails -> userDetails.getUsername().equals("Boris"))
                .verifyComplete();
    }

    // Негативный сценарий
    @Test
    void loadUserByUsername_UserNotFoundTest() {
        when(userRepository.findByLogin("unknownBoris")).thenReturn(Mono.empty());

        StepVerifier.create(userService.findByUsername("unknownBoris"))
                .expectError(UserNotFoundException.class)
                .verify();
    }
}
