package com.aston.userservice.service.impl;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.dto.UserDto;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import com.aston.userservice.event.UserCreatedEvent;
import com.aston.userservice.exception.RequisitesNotFoundException;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import com.aston.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Класс для работы с пользователем
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final RequisitesRepository requisitesRepository;
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    /**
     * Метод для получения пользователя по логину
     *
     * @param login login
     * @return пользователь
     */
    @Loggable
    @Override
    public UserProjection findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(
                        "Пользователь не найден по логину: " + login));
    }

    /**
     * Метод для получения реквизитов счета по id пользователя
     *
     * @param userId userId
     * @return реквизиты счета
     */
    @Loggable
    @Override
    public UserRequisitesProjection getUserRequisitesById(UUID userId) {
        return requisitesRepository.findByUserId(userId)
                .orElseThrow(() -> new RequisitesNotFoundException(String.format(
                        "Реквизиты счета не найдены по id пользователя: " + userId.toString())));
    }

    /**
     * Метод для сохранения пользователя в системе
     *
     * @param userDto пользователь системы
     * @return пользователь
     */
    @Loggable
    @Override
    public String createUser(UserDto userDto) throws ExecutionException, InterruptedException {
        //TODO save DB
        String userId = UUID.randomUUID().toString();
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userId, userDto.getFirstName(),
                userDto.getLastName(), userDto.getBirthday(), userDto.getInn(), userDto.getSnils(),
                userDto.getPassportNumber(), userDto.getLogin(), userDto.getPassword(), userDto.getRoles());

        SendResult<String, UserCreatedEvent> result = kafkaTemplate
                .send("user-created-events-topic", userId, userCreatedEvent).get();

        log.info("Топик: {}", result.getRecordMetadata().topic());
        log.info("Партиция: {}", result.getRecordMetadata().partition());
        log.info("Оффсет: {}", result.getRecordMetadata().offset());

        log.info("Возвращено: {}", userId);

        return userId;
    }
}
