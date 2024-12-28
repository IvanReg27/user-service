package com.aston.userservice.service;

import com.aston.userservice.domain.entity.User;
import com.aston.userservice.exception.UserNotFoundException;
import com.aston.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Класс для работы с пользователем
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
