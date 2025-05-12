package ru.makhorin.userservice.domain.response;

import lombok.Builder;

/**
 * DTO для направления реквизитов пользователя по id
 */
@Builder
public record UserRequisitesProjection(String firstName, String accountNumber, String kbk) {
}
