package com.aston.userservice.service;

import com.aston.userservice.annotation.Loggable;
import com.aston.userservice.domain.entity.Card;
import com.aston.userservice.domain.entity.UserRequisites;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.UserRequisitesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Сервис для работы с MongoDB
@Service
@RequiredArgsConstructor
@Slf4j
public class UserRequisitesService {
    private final UserRequisitesRepository userRequisitesRepository;

    @Loggable
    public UserRequisites saveUser(UserRequisites doc)  {
        return userRequisitesRepository.save(doc);
    }

    @Loggable
    public UserRequisites findById(String id) {
        return userRequisitesRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Реквизиты пользователя по id " + id + " не найдены"));
    }

    @Loggable
    public UserRequisites updateUserRequisites(String id, UserRequisites updateRequest) {
        var userRequisites = findById(id);
        updateRequest.getAccounts().forEach(
                account ->
                        userRequisites.getAccounts().stream()
                                .filter(oldAccount -> oldAccount.getId().equals(account.getId()))
                                .findFirst()
                                .ifPresentOrElse(
                                        oldAccount -> oldAccount.getCards().addAll(account.getCards()),
                                        () -> userRequisites.getAccounts().add(account)));

        return userRequisitesRepository.save(userRequisites);
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
                .flatMap(account -> account.getCards().stream())
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
