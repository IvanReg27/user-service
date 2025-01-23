package com.aston.userservice.domain.projection;

import org.springframework.data.rest.core.config.Projection;

/**
 * Проекция для извлечения реквизитов пользователя.
 */

public interface UserRequisitesProjection {

    String getFirstName();

    String getAccountNumber();

    String getKbk();
}
