package ru.makhorin.userservice.repository;

import ru.makhorin.userservice.domain.entity.User;
import ru.makhorin.userservice.domain.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс репозитория для работы с пользователем
 *
 * @see UserProjection
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Метод для получения пользователя по login
     *
     * @param login login пользователя
     * @return пользователь с заданным login
     */
    Optional<UserProjection> findByLogin(String login);

    /**
     * Метод для получения пользователя по Inn
     *
     * @param inn inn пользователя
     * @return пользователь с заданным inn
     */
    Optional<User> findByInn(String inn);

    /**
     * Метод для получения списка всех пользователей
     *
     * @return список всех пользователей
     */
    List<User> findAll();

    /**
     * Метод для проверки наличия логина пользователя в БД
     *
     * @param login login пользователя
     */
    boolean existsByLogin(String login);

    /**
     * Метод для удаления пользователя по логину
     *
     * @param login login пользователя
     */
    void deleteByLogin(String login);

    /**
     * Метод для поиска пользователя по id
     *
     * @param userId id пользователя
     */
    Optional<User> findById(UUID userId);
}
