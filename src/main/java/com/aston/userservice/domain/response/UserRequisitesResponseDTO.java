package com.aston.userservice.domain.response;

/**
 * DTO для направления необходимых реквизитов пользователя
 */
public record UserRequisitesResponseDTO (String firstName, Integer accountNumber, Integer kbk) {
}
