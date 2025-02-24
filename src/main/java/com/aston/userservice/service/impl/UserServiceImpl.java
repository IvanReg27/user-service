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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Класс для работы с пользователем
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final RequisitesRepository requisitesRepository;
    private final KafkaProducerServiceImpl kafkaProducerServiceImpl;
    private final PasswordEncoder passwordEncoder;

    @Loggable
    @Override
    public Mono<UserProjection> findByLogin(String login) {
        return userRepository.findByLogin(login)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Пользователь не найден по логину: " + login)));
    }

    @Loggable
    @Override
    public Mono<UserRequisitesProjection> getUserRequisitesById(UUID userId) {
        return requisitesRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new RequisitesNotFoundException(
                        "Реквизиты счета не найдены по id пользователя: " + userId)));
    }

    @Transactional
    @Loggable
    @Override
    public Mono<String> createUser(UserDto userDto) {
        return userRepository.findByInn(userDto.getInn())
                .flatMap(existingUser -> {
                    log.info("Пользователь с ИНН {} уже существует. Возвращаем существующий ID: {}", userDto.getInn(), existingUser.getId());
                    return Mono.just(existingUser.getId().toString());
                })
                .switchIfEmpty(Mono.defer(() -> {
                    User userEntity = User.builder()
                            .firstName(userDto.getFirstName())
                            .lastName(userDto.getLastName())
                            .birthday(userDto.getBirthday())
                            .inn(userDto.getInn())
                            .snils(userDto.getSnils())
                            .passportNumber(userDto.getPassportNumber())
                            .login(userDto.getLogin())
                            .password(passwordEncoder.encode(userDto.getPassword()))
                            .roles(userDto.getRoles())
                            .build();

                    return userRepository.save(userEntity)
                            .flatMap(savedUser -> {
                                log.info("Пользователь успешно сохранен в БД с id: {}", savedUser.getId());
                                kafkaProducerServiceImpl.sendUserCreatedEvent(savedUser);
                                return Mono.just(savedUser.getId().toString());
                            });
                })).onErrorMap(DataIntegrityViolationException.class, e -> {
                    log.warn("Попытка создать дубликат пользователя с ИНН {}", userDto.getInn());
                    return new ServiceException("Ошибка при создании нового пользователя", e);
                });
    }

    @Loggable
    @Override
    public Flux<UserProjection> getAllUsers() {
        return userRepository.findAllUsersBy();
    }

    /**
     * Метод для создания объекта(пользователя) по логину для
     * дальнейшего использования объета Spring Security для
     * аутентификации
     *
     * @param username логин пользователя
     * @return логин, пароль, роль
     */
    @Loggable
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByLogin(username)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Пользователь не найден: " + username)))
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getLogin(),
                        user.getPassword(),
                        user.getRoles()
                ));
    }
}
