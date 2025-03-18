package com.aston.userservice.service.impl;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.dto.RequisitesResponseDto;
import com.aston.userservice.domain.dto.UserResponseDto;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.entity.UserRoles;
import com.aston.userservice.domain.mapper.UserMapper;
import com.aston.userservice.domain.projection.UserProjection;
import com.aston.userservice.exception.RequisitesNotFoundException;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import com.aston.userservice.repository.UserRoleRepository;
import com.aston.userservice.security.Role;
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

import java.util.ArrayList;
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
    private final UserMapper userMapper;

    @Loggable
    @Override
    public Mono<UserProjection> findByLogin(String login) {
        return userRepository.findByLogin(login)
                .switchIfEmpty(Mono.error(new UserNotFoundException(
                        "Пользователь не найден по логину: " + login)));
    }

    @Loggable
    @Override
    public Mono<RequisitesResponseDto> getUserRequisitesById(Long userId) {
        return requisitesRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new RequisitesNotFoundException(
                        "Реквизиты счета не найдены по id пользователя: " + userId)));
    }

    @Transactional
    @Loggable
    @Override
    public Mono<String> createUser(UserResponseDto userDto) {
        // Реализация идемпотентности на уровне БД (перед сохранением в БД, проверяем, существует ли такой ИНН в БД)
        return userRepository.findByInn(userDto.getInn())
                .map(User::getId)
                .map(Object::toString)
                .doOnNext(id -> log.info("Пользователь с ИНН {} уже существует. ID: {}", userDto.getInn(), id))
                .switchIfEmpty(saveNewUser(userDto))
                .onErrorMap(DataIntegrityViolationException.class, e -> {
                    log.warn("Попытка создать дубликат пользователя с ИНН {}", userDto.getInn());
                    return new ServiceException("Ошибка при создании пользователя", e);
                });
    }

    private Mono<String> saveNewUser(UserResponseDto userDto) {
        // Создаем нового пользователя, если не найдено совпадений по ИНН
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Принудительно добавляем роль "USER", игнорируя роли из запроса клиента
        List<String> roles = new ArrayList<>();
        roles.add("USER");

        return userRepository.save(user)
                .flatMap(savedUser -> saveUserRoles(savedUser, roles)
                        .thenReturn(savedUser.getId().toString())
                        .doOnSuccess(id -> {
                            log.info("Пользователь сохранен в базу данных под id {}", id);
                            kafkaProducerServiceImpl.sendUserCreatedEvent(savedUser);
                        })
                );
    }

    private Mono<Void> saveUserRoles(User savedUser, List<String> roles) {
        // Преобразуем роль(и) в соответствующую сущность UserRoles
        List<UserRoles> userRoles = roles.stream()
                .map(role -> new UserRoles(savedUser.getId(), role))
                .collect(Collectors.toList());

        return userRoleRepository.saveAll(userRoles).then();
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
                .switchIfEmpty(Mono.error(new UserNotFoundException("Пользователь не найден: "
                        + username)))
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getLogin(),
                        user.getPassword(),
                        user.getRoles()
                ));
    }
}
