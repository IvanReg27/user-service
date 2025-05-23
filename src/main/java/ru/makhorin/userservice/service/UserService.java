package ru.makhorin.userservice.service;

import ru.makhorin.userservice.domain.dto.UserDto;
import ru.makhorin.userservice.domain.projection.UserProjection;
import ru.makhorin.userservice.domain.projection.UserRequisitesProjection;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс для работы с пользователем
 */
public interface UserService {

    /**
     * Метод для получения пользователя по логину
     *
     * @param login login пользователя
     * @return пользователь
     */
    UserProjection findByLogin(String login);

    /**
     * Метод для получения реквизитов счета по id пользователя
     *
     * @param userId userId пользователя
     * @return реквизиты
     */
    UserRequisitesProjection getUserRequisitesById(UUID userId);

    /**
     * Метод для сохранения пользователя в системе
     *
     * @param userDto пользователь системы
     * @return пользователь
     */
    String createUser(UserDto userDto);

    /**
     * Метод для получения списка всех пользователей
     *
     * @return список всех пользователей
     */
    List<UserDto> findAllUsers();

    /**
     * Метод для удаления пользователя по логину
     *
     * @param login login пользователя
     */
    void deleteByLogin(String login);

    /**
     * Метод для обновления пользователя по id
     *
     * @param userId id пользователя
     */
    String updateUser(UUID userId, UserDto userDto);
}
