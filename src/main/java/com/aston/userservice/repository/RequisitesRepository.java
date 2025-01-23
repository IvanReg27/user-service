package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.Requisites;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс репозитория для работы с реквизитами счета пользователя
 *
 * @see Requisites
 */
@Repository
public interface RequisitesRepository extends JpaRepository<Requisites, UUID> {

    /**
     * Метод для получения пользователя по id
     *
     * @param userId userId пользователя
     * @return пользователь с заданным id
     */
    @Query("select u.firstName as firstName, trim(r.accountNumber) as accountNumber, trim(r.kbk) as kbk " +
            "from Requisites r join r.user u where u.id = :userId")
    Optional<UserRequisitesProjection> findByUserId(@Param("userId") UUID userId);
}
