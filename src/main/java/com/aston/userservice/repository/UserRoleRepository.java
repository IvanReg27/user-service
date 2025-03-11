package com.aston.userservice.repository;

import com.aston.userservice.domain.entity.UserRole;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRoleRepository extends R2dbcRepository<UserRole, UUID> {

    @Query("SELECT role FROM user_roles WHERE user_id = :userId")
    Flux<String> findRolesByUserId(UUID userId);

    @Modifying
    @Query("INSERT INTO user_roles (user_id, role) VALUES (:userId, :role)")
    Mono<Void> saveRole(UUID userId, String role);
}
