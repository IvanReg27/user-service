package com.aston.userservice.service.impl;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.dto.UserDto;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import com.aston.userservice.exception.RequisitesNotFoundException;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import com.aston.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Класс для работы с пользователем
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final RequisitesRepository requisitesRepository;
    private final KafkaProducerService kafkaProducerService;

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
    @Transactional
    @Loggable
    @Override
    public String createUser(UserDto userDto) {
        try {
            User userEntity = User.builder()
                    .firstName(userDto.getFirstName())
                    .lastName(userDto.getLastName())
                    .birthday(userDto.getBirthday())
                    .inn(userDto.getInn())
                    .snils(userDto.getSnils())
                    .passportNumber(userDto.getPassportNumber())
                    .login(userDto.getLogin())
                    .password(userDto.getPassword())
                    .roles(userDto.getRoles())
                    .build();

            userEntity = userRepository.save(userEntity);
            log.info("Пользователь сохранен в БД с id: {}", userEntity.getId());

            // Создаем событие(сообщение) для Kafka, используя сгенерированный в БД userId
            kafkaProducerService.sendUserCreatedEvent(userEntity);

            return userEntity.getId().toString();
        } catch (Exception e) {
            log.error("Ошибка при создании нового пользователя: {}", e.getMessage(), e);
            throw new ServiceException("Ошибка при создании нового пользователя", e);
        }
    }
}
