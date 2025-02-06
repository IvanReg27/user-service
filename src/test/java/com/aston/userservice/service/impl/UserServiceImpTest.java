package com.aston.userservice.service.impl;

import com.aston.userservice.domain.dto.UserDto;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import com.aston.userservice.exception.RequisitesNotFoundException;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RequisitesRepository requisitesRepository;
    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private UserServiceImp userService;

    private User user;
    private UserProjection userProjection;
    private UserRequisitesProjection requisitesProjection;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin("Boris");

        userProjection = mock(UserProjection.class);
        requisitesProjection = mock(UserRequisitesProjection.class);
    }

    //Позитивный сценарий
    @Test
    void findByLoginPositiveTest() {
        when(userRepository.findByLogin("Boris")).thenReturn(Optional.of(userProjection));

        UserProjection result = userService.findByLogin("Boris");

        assertNotNull(result);
        verify(userRepository, times(1)).findByLogin("Boris");
    }

    //Негативный сценарий
    @Test
    void findByLoginNegativeTest() {
        when(userRepository.findByLogin("unknownBoris")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByLogin("unknownBoris"));
        verify(userRepository, times(1)).findByLogin("unknownBoris");
    }

    //Позитивный сценарий
    @Test
    void getUserRequisitesByIdPositiveTest() {
        UUID userId = UUID.randomUUID();
        when(requisitesRepository.findByUserId(userId)).thenReturn(Optional.of(requisitesProjection));

        UserRequisitesProjection result = userService.getUserRequisitesById(userId);

        assertNotNull(result);
        verify(requisitesRepository, times(1)).findByUserId(userId);
    }

    //Негативный сценарий
    @Test
    void getUserRequisitesByIdNegativeTest() {
        UUID userId = UUID.randomUUID();
        when(requisitesRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(RequisitesNotFoundException.class, () -> userService.getUserRequisitesById(userId));
        verify(requisitesRepository, times(1)).findByUserId(userId);
    }

    //Позитивный сценарий
    @Test
    void createUserPositiveTest() {
        UserDto userDto = new UserDto();
        userDto.setLogin("Boris");

        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(user.getId().toString(), result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    //Негативный сценарий
    @Test
    void createUserNegativeTest() {
        UserDto userDto = new UserDto();
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Ошибка в БД"));

        assertThrows(ServiceException.class, () -> userService.createUser(userDto));
        verify(userRepository, times(1)).save(any(User.class));
    }
}
