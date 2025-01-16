package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.Requisites;
import com.aston.userservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс репозитория для работы с реквизитами счета пользователя
 *
 * @see Requisites
 */
@Repository
public interface RequisitesRepository extends JpaRepository<Requisites, Integer> {

    /**
     * Метод для получения пользователя по id
     *
     * @param userId userId пользователя
     * @return пользователь с заданным id
     */
    Optional<Requisites> findByUserId(UUID userId);
}
