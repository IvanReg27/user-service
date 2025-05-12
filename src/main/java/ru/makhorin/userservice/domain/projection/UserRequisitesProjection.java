package ru.makhorin.userservice.domain.projection;

/**
 * Проекция для извлечения реквизитов пользователя.
 */

public interface UserRequisitesProjection {

    String getFirstName();

    String getAccountNumber();

    String getKbk();
}
