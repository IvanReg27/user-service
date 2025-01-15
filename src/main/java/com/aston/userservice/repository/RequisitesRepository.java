package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.Requisites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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
