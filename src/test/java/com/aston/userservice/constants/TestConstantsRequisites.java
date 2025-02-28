package com.aston.userservice.constants;

import com.aston.userservice.domain.entity.Requisites;

import java.util.UUID;

public class TestConstantsRequisites {

    public static final Requisites REQUISITES = Requisites.builder()
            .id(UUID.randomUUID())
            .accountNumber("12345678901234567890")
            .bik("044525225")
            .correspondentCheck("30101810400000000225")
            .inn("7807083893")
            .kpp("780701001")
            .kbk("18210102010011000110")
            .userId(TestConstantsUser.USER.getId())
            .build();
}
