package com.aston.userservice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Класс сущность, соответствующий таблице Requisites в UserDB
 */
@Table("requisites")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class Requisites {

    @Id
    private Long id;

    private String accountNumber;

    private String bik;

    private String correspondentCheck;

    private String inn;

    private String kpp;

    private String kbk;

    @Column("user_id")
    private Long userId;
}
