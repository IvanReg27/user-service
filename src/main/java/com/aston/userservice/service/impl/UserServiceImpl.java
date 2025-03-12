package com.aston.userservice.service.impl;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.dto.UserDto;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.entity.UserRole;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import com.aston.userservice.exception.RequisitesNotFoundException;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import com.aston.userservice.repository.UserRoleRepository;
import com.aston.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.shaded.com.google.protobuf.ServiceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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
    private final UserRoleRepository userRoleRepository;

    @Loggable
    @Override
    public Mono<UserProjection> findByLogin(String login) {
        return userRepository.findByLogin(login)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Пользователь не найден по логину: " + login)));
    }

    @Loggable
    @Override
    public Mono<UserRequisitesProjection> getUserRequisitesById(Long userId) {
        return requisitesRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new RequisitesNotFoundException(
                        "Реквизиты счета не найдены по id пользователя: " + userId)));
    }

//    @Transactional
    @Loggable
    @Override
    public Mono<String> createUser(UserDto userDto) {
        // Реализация идемпотентности на уровне БД (перед сохранением в БД, проверяем, существует ли такой ИНН в БД)
        return userRepository.findByInn(userDto.getInn())
                .flatMap(existingUser -> {
                    // Если пользователь с таким ИНН уже существует, возвращаем его ID
                    log.info("Пользователь с ИНН {} уже существует. Возвращаем существующий ID: {}", userDto.getInn(), existingUser.getId());
                    return Mono.just(existingUser.getId().toString());
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // Создаем нового пользователя, если не найдено совпадений по ИНН
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

                                // Преобразуем роли в соответствующие сущности UserRole
                                List<UserRole> roles = userDto.getRoles().stream()
                                        .map(role -> new UserRole(savedUser.getId(), role.name()))
                                        .collect(Collectors.toList());

                                // Сохраняем роли пользователя в базе данных
                                return userRoleRepository.saveAll(roles)
                                        .then(Mono.defer(() -> {
                                            // После успешного сохранения пользователя и его ролей, отправляем событие в Kafka
                                            kafkaProducerServiceImpl.sendUserCreatedEvent(savedUser);
                                            log.info("Событие о создании нового пользователя c id: {} отправлено в Kafka", savedUser.getId());
                                            return Mono.just(savedUser.getId().toString());
                                        }));
                            });
                }))
                .onErrorMap(DataIntegrityViolationException.class, e -> {
                    // Если возникает ошибка при сохранении, например, из-за дублирования данных в БД
                    log.warn("Попытка создать дубликат пользователя с ИНН {}", userDto.getInn());
                    return new ServiceException("Ошибка при создании нового пользователя", e);
                })
                .onErrorResume(e -> {
                    // Логируем и возвращаем ошибку, если возникла другая непредвиденная ошибка
                    log.error("Ошибка при создании пользователя: {}", e.getMessage());
                    return Mono.error(new ServiceException("Неизвестная ошибка при создании пользователя", e));
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
