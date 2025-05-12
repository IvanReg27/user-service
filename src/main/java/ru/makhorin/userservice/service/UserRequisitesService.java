package ru.makhorin.userservice.service;

import ru.makhorin.userservice.annotation.Loggable;
import ru.makhorin.userservice.domain.dto.CardDto;
import ru.makhorin.userservice.domain.dto.UserRequisitesDto;
import ru.makhorin.userservice.domain.mapper.UserRequisitesMapper;
import ru.makhorin.userservice.exception.UserNotFoundException;
import ru.makhorin.userservice.repository.UserRequisitesRepository;
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
    private final UserRequisitesMapper userRequisitesMapper;

    @Loggable
    public UserRequisitesDto saveUser(UserRequisitesDto dto) {
        var entity = userRequisitesMapper.toEntity(dto);
        var saved = userRequisitesRepository.save(entity);

        return userRequisitesMapper.toDto(saved);
    }

    @Loggable
    public UserRequisitesDto findById(String id) {
        var entity = userRequisitesRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Реквизиты пользователя по id " + id + " не найдены"));

        return userRequisitesMapper.toDto(entity);
    }

    @Loggable
    public UserRequisitesDto updateUserRequisites(String id, UserRequisitesDto updateDto) {
        var existingEntity = userRequisitesRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Реквизиты пользователя по id " + id + " не найдены"));

        var updateEntity = userRequisitesMapper.toEntity(updateDto);

        updateEntity.getAccounts().forEach(newAccount ->
                existingEntity.getAccounts().stream()
                        .filter(oldAccount -> Objects.equals(oldAccount.getNumber(), newAccount.getNumber()))
                        .findFirst()
                        .ifPresentOrElse(
                                oldAccount -> oldAccount.getCards().addAll(newAccount.getCards()),
                                () -> existingEntity.getAccounts().add(newAccount)
                        )
        );
        var updated = userRequisitesRepository.save(existingEntity);

        return userRequisitesMapper.toDto(updated);
    }

    @Loggable
    public UserRequisitesDto getUserById(String userId) {
        var entity = userRequisitesRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Реквизиты пользователя по userId " + userId + " не найдены"));

        return userRequisitesMapper.toDto(entity);
    }

    @Loggable
    public List<CardDto> getUserCards(String userId, boolean expiringSoon) {
        var entity = userRequisitesRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Реквизиты пользователя по userId " + userId + " не найдены"));

        List<CardDto> allCards = entity.getAccounts().stream()
                .filter(Objects::nonNull)
                .flatMap(account -> account.getCards().stream())
                .map(userRequisitesMapper::toCardDto)
                .collect(Collectors.toList());

        if (expiringSoon) {
            LocalDate now = LocalDate.now();
            return allCards.stream()
                    .filter(card -> card.getExpirationDate() != null)
                    .filter(card -> card.getExpirationDate().isAfter(now) &&
                            card.getExpirationDate().isBefore(now.plusDays(30)))
                    .collect(Collectors.toList());
        }

        return allCards;
    }
}
