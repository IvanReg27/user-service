package com.aston.userservice.service.impl;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.dto.UserDto;
import com.aston.userservice.domain.entity.User;
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
import org.springframework.transaction.annotation.Transactional;

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
     * Производитель, отправляет события(сообщения) в брокер
     *
     */
    @Transactional
    @Loggable
    @Override
    public String createUser(UserDto userDto) throws ExecutionException, InterruptedException {
        User userEntity = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .birthday(userDto.getBirthday())
                .inn(userDto.getInn())
                .snils(userDto.getSnils())
                .passportNumber(userDto.getPassportNumber())
                .login(userDto.getLogin())
                .password(userDto.getPassword()) // Пароль сохраняется как есть
                .roles(userDto.getRoles())
                .build();

        userEntity = userRepository.save(userEntity);
        log.info("Пользователь сохранен в БД с id: {}", userEntity.getId());

        /**
         * Создаем событие(сообщение) для Kafka, используя сгенерированный в БД userId
         *
         */
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userEntity.getId().toString(),
                userEntity.getFirstName(), userEntity.getLastName(), userEntity.getBirthday(),
                userEntity.getInn(), userEntity.getSnils(), userEntity.getPassportNumber(),
                userEntity.getLogin(), userEntity.getPassword(), userEntity.getRoles());

        SendResult<String, UserCreatedEvent> result = kafkaTemplate
                .send("user-created-events-topic", userEntity.getId().toString(), userCreatedEvent).get();

        log.info("Топик: {}", result.getRecordMetadata().topic());
        log.info("Партиция: {}", result.getRecordMetadata().partition());
        log.info("Оффсет: {}", result.getRecordMetadata().offset());

        log.info("Сообщение доставлено брокеру, подтверждено id: {}", userEntity.getId());

        return userEntity.getId().toString();
    }
}
