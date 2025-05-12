package ru.makhorin.userservice.service;

import ru.makhorin.userservice.domain.dto.AccountDto;
import ru.makhorin.userservice.domain.dto.CardDto;
import ru.makhorin.userservice.domain.dto.UserRequisitesDto;
import ru.makhorin.userservice.domain.entity.Account;
import ru.makhorin.userservice.domain.entity.Card;
import ru.makhorin.userservice.domain.entity.UserRequisites;
import ru.makhorin.userservice.exception.UserNotFoundException;
import ru.makhorin.userservice.repository.UserRequisitesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Интеграционные тесты (MongoDB)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserRequisitesServiceTest {

    @Autowired
    private UserRequisitesService userRequisitesService;
    @Autowired
    private UserRequisitesRepository userRequisitesRepository;

    @BeforeEach
    void clean() {
        userRequisitesRepository.deleteAll();
    }

    // Позитивные сценарии
    @Test
    void  saveUserTest() {
        UserRequisitesDto saveUserDto = UserRequisitesDto.builder()
                .userId("user123")
                .accounts(List.of(
                        AccountDto.builder()
                                .number("123456789")
                                .balance(BigDecimal.valueOf(250000.0))
                                .openDate(LocalDate.now())
                                .cards(List.of(
                                        CardDto.builder()
                                                .number("1111-2222-3333-4444")
                                                .expirationDate(LocalDate.now().plusYears(2))
                                                .issueDate(LocalDate.now())
                                                .type(Card.CardType.DEBIT)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        UserRequisitesDto saved = userRequisitesService.saveUser(saveUserDto);

        assertNotNull(saved);
        assertEquals("user123", saved.getUserId());
        assertEquals(1, saved.getAccounts().size());
        assertEquals(1, saved.getAccounts().get(0).getCards().size());
    }

    @Test
    void updateUserRequisitesTest() {
        UserRequisites initialEntity = UserRequisites.builder()
                .userId("user456")
                .accounts(new ArrayList<>())
                .build();
        initialEntity = userRequisitesRepository.save(initialEntity);

        UserRequisitesDto updateUserDto = UserRequisitesDto.builder()
                .accounts(List.of(
                        AccountDto.builder()
                                .number("999999")
                                .balance(BigDecimal.valueOf(1500000.0))
                                .openDate(LocalDate.now())
                                .cards(List.of(
                                        CardDto.builder()
                                                .number("5555-6666-7777-8888")
                                                .expirationDate(LocalDate.now().plusMonths(6))
                                                .issueDate(LocalDate.now())
                                                .type(Card.CardType.CREDIT)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        UserRequisitesDto updated = userRequisitesService.updateUserRequisites(initialEntity.getId(), updateUserDto);

        assertEquals(1, updated.getAccounts().size());
        assertEquals("999999", updated.getAccounts().get(0).getNumber());
        assertEquals(1, updated.getAccounts().get(0).getCards().size());
    }

    @Test
    void getUserByIdTest() {
        UserRequisites entity = UserRequisites.builder()
                .userId("user789")
                .accounts(new ArrayList<>())
                .build();

        userRequisitesRepository.save(entity);

        UserRequisitesDto foundUserDto = userRequisitesService.getUserById("user789");

        assertNotNull(foundUserDto);
        assertEquals("user789", foundUserDto.getUserId());
    }

    @Test
    void getUserCardsTest() {
        LocalDate now = LocalDate.now();
        List<Card> cards = List.of(
                Card.builder()
                        .id("1")
                        .number("1111")
                        .expirationDate(now.plusDays(10))
                        .issueDate(now.minusYears(1))
                        .type(Card.CardType.DEBIT)
                        .build(),
                Card.builder()
                        .id("2")
                        .number("2222")
                        .expirationDate(now.plusMonths(6))
                        .issueDate(now.minusYears(2))
                        .type(Card.CardType.CREDIT)
                        .build()
        );

        Account account = Account.builder()
                .id("acc2")
                .number("555555")
                .balance(BigDecimal.valueOf(0.0))
                .openDate(now)
                .cards(cards)
                .build();

        UserRequisites entity = UserRequisites.builder()
                .userId("expiringUser")
                .accounts(List.of(account))
                .build();

        userRequisitesRepository.save(entity);

        List<CardDto> expiringCard = userRequisitesService.getUserCards("expiringUser", true);

        assertEquals(1, expiringCard.size());
        assertEquals("1111", expiringCard.get(0).getNumber());
    }

    // Негативный сценарий
    @Test
    void getUserByIdNotFoundTest() {
        assertThrows(UserNotFoundException.class, () -> {
            userRequisitesService.getUserById("Реквизиты пользователя отсутствуют ");
        });
    }
}
