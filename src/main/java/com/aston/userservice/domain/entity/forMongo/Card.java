package com.aston.userservice.domain.entity.forMongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
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
