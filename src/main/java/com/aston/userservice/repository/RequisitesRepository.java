package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.Requisites;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface RequisitesRepository extends ReactiveCrudRepository<Requisites, UUID> {

    /**
     * Метод для получения пользователя по id
     *
     * @param userId userId пользователя
     * @return пользователь с заданным id в виде проекции
     */
    @Query("SELECT u.first_name AS firstName, TRIM(r.account_number) AS accountNumber, TRIM(r.kbk) AS kbk " +
            "FROM requisites r JOIN users u ON r.user_id = u.id WHERE u.id = :userId")
    Mono<UserRequisitesProjection> findByUserId(UUID userId);
}
