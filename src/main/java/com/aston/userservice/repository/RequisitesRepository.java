package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.Requisites;
import com.aston.userservice.domain.projection.UserRequisitesProjection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface RequisitesRepository extends R2dbcRepository<Requisites, UUID> {

    /**
     * Метод для получения информации о счете по id пользователя
     *
     * @param userId Id пользователя
     * @return Информацию о счете {@link UserRequisitesProjection}
     */
    @Query("SELECT u.first_name, r.account_number, r.kbk " +
            "FROM requisites r INNER JOIN users u ON r.user_id = u.id WHERE u.id = :userId")
    Mono<UserRequisitesProjection> findByUserId(Long userId);
}
