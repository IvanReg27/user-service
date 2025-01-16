package com.aston.userservice.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс сущность, соответствующий таблице Requisites в UserDB
 */
@Entity
@Table(name = "requisites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requisites {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_number", nullable = false, length = 30)
    private String accountNumber;

    @Column(name = "bik", nullable = false, length = 10)
    private String bik;

    @Column(name = "correspondent_check", nullable = false, length = 20)
    private String correspondentCheck;

    @Column(name = "inn", nullable = false, length = 10)
    private String inn;

    @Column(name = "kpp", nullable = false, length = 9)
    private String kpp;

    @Column(name = "kbk", nullable = false, length = 20)
    private String kbk;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
