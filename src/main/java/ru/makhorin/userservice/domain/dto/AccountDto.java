package ru.makhorin.userservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class AccountDto {
    private String id;
    private String number;
    private BigDecimal balance;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate openDate;
    private List<CardDto> cards;
}
