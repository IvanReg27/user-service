package com.aston.userservice.domain.response;

import lombok.Builder;

import java.math.BigInteger;

/**
 * DTO для направления реквизитов пользователя
 */
@Builder
public record UserRequisitesResponseDTO(String firstName, BigInteger accountNumber, BigInteger kbk) {
}
