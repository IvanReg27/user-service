package com.aston.userservice.domain.entity.forMongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class Account {
    private String id;
    private String number;
    private double balance;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate openDate;
    private List<Card> cards;
}
