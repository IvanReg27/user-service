package ru.makhorin.userservice.constants;

import ru.makhorin.userservice.domain.entity.Requisites;

public class TestConstantsRequisites {

    public static final Requisites REQUISITES = Requisites.builder()
            .accountNumber("12345678901234567890")
            .bik("044525225")
            .correspondentCheck("30101810400000000225")
            .inn("7807083893")
            .kpp("780701001")
            .kbk("18210102010011000110")
            .user(TestConstantsUser.USER)
            .build();
}
