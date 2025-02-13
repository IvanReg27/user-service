package com.aston.userservice.service.impl;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

/**
 * Создаем событие(сообщение) для Kafka, используя сгенерированный в БД userId
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public void sendUserCreatedEvent(User userEntity) {
        try {
            UserCreatedEvent userCreatedEvent = new UserCreatedEvent(
                    userEntity.getId().toString(),
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    userEntity.getBirthday(),
                    userEntity.getInn(),
                    userEntity.getSnils(),
                    userEntity.getPassportNumber(),
                    userEntity.getLogin(),
                    userEntity.getPassword(),
                    userEntity.getRoles()
            );

            SendResult<String, UserCreatedEvent> result = kafkaTemplate
                    .send("user-created-events-topic", userEntity.getId().toString(), userCreatedEvent).get();

            log.info("Топик: {}", result.getRecordMetadata().topic());
            log.info("Партиция: {}", result.getRecordMetadata().partition());
            log.info("Оффсет: {}", result.getRecordMetadata().offset());
            log.info("Сообщение доставлено брокеру, подтверждено id: {}", userEntity.getId());

        } catch (ExecutionException | InterruptedException e) {
            log.error("Ошибка при отправке сообщения в брокер: {}", e.getMessage(), e);
            throw new ServiceException("Ошибка при отправке события о создании нового пользователя", e);
        }
    }
}
