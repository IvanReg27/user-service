package com.aston.userservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// Сущность для работы с MongoDB
@Data
@Builder(toBuilder = true)
public class Card {
    private String id;
    private String number;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate issueDate;
    private CardType type;

    public enum CardType {
        DEBIT, CREDIT
    }
}
