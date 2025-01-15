package com.aston.userservice.domain.response;

import lombok.Builder;

/**
 * DTO для направления необходимых реквизитов пользователя
 */
@Builder
public record UserRequisitesResponseDTO (String firstName, Integer accountNumber, Integer kbk) {
}
