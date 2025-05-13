package ru.makhorin.userservice.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class UserRequisitesDto {
    private String id;
    private String userId;
    private List<AccountDto> accounts;
}
