package ru.makhorin.userservice.event.usercreated;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer, получает события(сообщения) из брокера
 *
 */
@Component
@KafkaListener(topics = "user-created-events-topic")
@Slf4j
public class UserCreatedEventHandler {

    @KafkaHandler
    public void handle(UserCreatedEvent userCreatedEvent) {
        log.info("Сообщение получено из брокера, подтверждено ФИО: {} {}", userCreatedEvent.getFirstName(),
                userCreatedEvent.getLastName());
    }
}
