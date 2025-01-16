package com.aston.userservice.service.impl;

import com.aston.userservice.domain.entity.Requisites;
import com.aston.userservice.domain.entity.User;
import com.aston.userservice.domain.response.UserRequisitesResponseDTO;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.RequisitesRepository;
import com.aston.userservice.repository.UserRepository;
import com.aston.userservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Класс для работы с пользователем
 */
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final RequisitesRepository requisitesRepository;

    /**
     * Метод для получения пользователя по логину
     *
     * @param login login
     * @return пользователь
     */
    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    /**
     * Метод для получения реквизитов счета по id пользователя
     *
     * @param userId userId
     * @return реквизиты счета
     */
    @Override
    public UserRequisitesResponseDTO getUserRequisitesById(UUID userId) {

        Requisites requisites = requisitesRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Реквизиты не найдены по id пользователя: " + userId));

        return UserRequisitesResponseDTO.builder()
                .firstName(requisites.getUser().getFirstName())
                .accountNumber(requisites.getAccountNumber())
                .kbk(requisites.getKbk())
                .build();
    }
}
