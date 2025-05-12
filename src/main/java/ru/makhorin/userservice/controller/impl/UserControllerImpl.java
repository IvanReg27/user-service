package ru.makhorin.userservice.controller.impl;

import ru.makhorin.userservice.annotation.Loggable;
import ru.makhorin.userservice.controller.UserController;
import ru.makhorin.userservice.domain.dto.UserDto;
import ru.makhorin.userservice.domain.projection.UserProjection;
import ru.makhorin.userservice.domain.projection.UserRequisitesProjection;
import ru.makhorin.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Loggable
    @Override
    public ResponseEntity<UserProjection> getUser(@PathVariable String login) {
        return ResponseEntity.ok(userService.findByLogin(login));
    }

    @Loggable
    @Override
    public ResponseEntity<UserRequisitesProjection> getUserRequisites(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserRequisitesById(id));
    }

    @Loggable
    @Override
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        String userId = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @Loggable
    @Override
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @Loggable
    @Override
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        userService.deleteByLogin(login);
        return ResponseEntity.noContent().build();
    }

    @Loggable
    @Override
    public ResponseEntity<Object> updateUser(@PathVariable UUID userId, @RequestBody UserDto userDto) {
        String updatedUserId = userService.updateUser(userId, userDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUserId);
    }
}
