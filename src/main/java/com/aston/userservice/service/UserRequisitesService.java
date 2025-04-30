package com.aston.userservice.service;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.entity.Account;
import com.aston.userservice.domain.entity.Card;
import com.aston.userservice.domain.entity.UserRequisites;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.UserRequisitesRepository;
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

// Сервис для работы с MongoDB
@Service
@RequiredArgsConstructor
@Slf4j
public class UserRequisitesService {
    private final UserRequisitesRepository userRequisitesRepository;

    @Loggable
    public UserRequisites createUser(UserRequisites doc)  {
        return userRequisitesRepository.save(doc);
    }

    @Loggable
    public UserRequisites updateUserRequisites(String id, UserRequisites updateRequest) {
        return userRequisitesRepository.findById(id).map(existing -> {
            if (updateRequest.getAccounts() != null) {
                // Создаем список счетов, если null
                if (existing.getAccounts() == null) {
                    existing.setAccounts(new ArrayList<>());
                }
                // Обрабатываем счета через Stream
                updateRequest.getAccounts().forEach(newAcc -> {
                    existing.getAccounts()
                            .stream()
                            .filter(acc -> acc.getId().equals(newAcc.getId()))
                            .findFirst()
                            .ifPresentOrElse(existingAcc -> {
                                        // Обновляем карты для существующего счета
                                        if (newAcc.getCards() != null) {
                                            if (existingAcc.getCards() == null) {
                                                existingAcc.setCards(new ArrayList<>());
                                            }
                                            existingAcc.getCards().addAll(newAcc.getCards());
                                        }
                                    },
                                    () -> existing.getAccounts().add(newAcc)
                            );
                });
            }
            return userRequisitesRepository.save(existing);
        }).orElseThrow(() -> new UserNotFoundException("Реквизиты пользователя по id " + id + " не найдены"));
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
