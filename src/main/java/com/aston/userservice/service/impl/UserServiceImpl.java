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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Класс для работы с пользователем
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RequisitesRepository requisitesRepository;
    private final KafkaProducerServiceImpl kafkaProducerServiceImpl;
    private final PasswordEncoder passwordEncoder;


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
            //Реализация идемпотентности на уровне БД (перед сохранением в БД, проверяем, существует ли такой ИНН в БД)
            Optional<User> existingUser = userRepository.findByInn(userDto.getInn());
            if (existingUser.isPresent()) {
                log.info("Пользователь с ИНН {} уже существует. Возвращаем существующий ID: {}",
                        userDto.getInn(), existingUser.get().getId());
                return existingUser.get().getId().toString();
            }

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

            userEntity = userRepository.save(userEntity);
            userRepository.flush();
            log.info("Пользователь успешно сохранен в БД с id: {}", userEntity.getId());

            // Создаем событие(сообщение) для Kafka, используя сгенерированный в БД userId
            kafkaProducerServiceImpl.sendUserCreatedEvent(userEntity);

            return userEntity.getId().toString();

        } catch (DataIntegrityViolationException e) {
            log.warn("Попытка создать дубликат пользователя с ИНН {}", userDto.getInn());
            Optional<User> existingUser = userRepository.findByInn(userDto.getInn());
            return existingUser.map(user -> user.getId().toString()).orElseThrow(() ->
                    new ServiceException("Ошибка при проверке существующего пользователя", e));
        } catch (Exception e) {
            log.error("Ошибка при создании нового пользователя: {}", e.getMessage(), e);
            throw new ServiceException("Ошибка при создании нового пользователя", e);
        }
    }

    @Loggable
    @Cacheable(value = "findAllUsersCache")
    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAllBy();
        if (users.isEmpty()) {
            throw new UserNotFoundException("Пользователи отсутствуют в базе данных");
        }
        return users.stream().map(user -> UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthday(user.getBirthday())
                .inn(user.getInn())
                .snils(user.getSnils())
                .passportNumber(user.getPassportNumber())
                .login(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    @Loggable
    @Override
    public void deleteByLogin(String login) {
        boolean exists = userRepository.existsByLogin(login);
        if (!exists) {
            throw new UserNotFoundException("Пользователь не найден по логину: " + login);
        }
        userRepository.deleteByLogin(login);
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserProjection user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                user.getRoles()
        );
    }
}
