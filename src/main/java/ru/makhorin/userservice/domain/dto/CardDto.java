package ru.makhorin.userservice.domain.dto;

import ru.makhorin.userservice.domain.entity.Card;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class CardDto {
    private String id;
    private String number;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate issueDate;
    private Card.CardType type;


    public enum CardType {
        DEBIT, CREDIT
    }
}
