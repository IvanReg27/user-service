package com.aston.userservice.service.forMongo;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.entity.forMongo.Account;
import com.aston.userservice.domain.entity.forMongo.Card;
import com.aston.userservice.domain.entity.forMongo.UserRequisites;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.forMongo.UserRequisitesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRequisitesService {
    private final UserRequisitesRepository userRequisitesRepository;

    @Loggable
    public UserRequisites createUser(UserRequisites doc) {
        return userRequisitesRepository.save(doc);
    }

    public UserRequisites updateUserRequisites(String id, UserRequisites updateRequest) {
        return userRequisitesRepository.findById(id).map(existing -> {
            // Добавляем или обновляем счета
            if (updateRequest.getAccounts() != null) {
                for (Account newAcc : updateRequest.getAccounts()) {
                    Optional<Account> existingAccountOpt = existing.getAccounts()
                            .stream()
                            .filter(acc -> acc.getId().equals(newAcc.getId()))
                            .findFirst();

                    if (existingAccountOpt.isPresent()) {
                        // Обновляем карты для существующего счета
                        Account existingAccount = existingAccountOpt.get();
                        if (newAcc.getCards() != null) {
                            if (existingAccount.getCards() == null) {
                                existingAccount.setCards(new ArrayList<>());
                            }
                            existingAccount.getCards().addAll(newAcc.getCards());
                        }
                    } else {
                        // Добавляем новый счет
                        if (existing.getAccounts() == null) {
                            existing.setAccounts(new ArrayList<>());
                        }
                        existing.getAccounts().add(newAcc);
                    }
                }
            }
            return userRequisitesRepository.save(existing);
        }).orElseThrow(() -> new UserNotFoundException("Реквизиты пользователь по id " + id + " не найдены"));
    }
    @Loggable
    public UserRequisites getUserById(String userId) {
        return userRequisitesRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Реквизиты пользователь по userId " + userId + " не найдены"));
    }
    @Loggable
    public List<Card> getUserCards(String userId, boolean expiringSoon) {
        UserRequisites userRequisites = userRequisitesRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Реквизиты пользователь по userId " + userId + " не найдены"));

        List<Card> allCards = userRequisites.getAccounts()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(account -> account.getCards() != null ? account.getCards().stream() : Stream.empty())
                .collect(Collectors.toList());

        if (expiringSoon) {
            LocalDate now = LocalDate.now();
            return allCards
                    .stream()
                    .filter(card -> card.getExpirationDate() != null)
                    .filter(card -> card.getExpirationDate().isBefore(now) &&
                            card.getExpirationDate().isBefore(now.plusDays(30)))
                    .collect(Collectors.toList());
        }
        return allCards;
    }
}
