//package com.aston.userservice.service;
//
//import com.aston.userservice.constants.TestConstantsRequisites;
//import com.aston.userservice.constants.TestConstantsUser;
//import com.aston.userservice.domain.entity.Requisites;
//import com.aston.userservice.domain.entity.User;
//import com.aston.userservice.exception.RequisitesNotFoundException;
//import com.aston.userservice.integration.cofig.IntegrationTest;
//import com.aston.userservice.integration.cofig.PostgresTestContainer;
//import com.aston.userservice.repository.RequisitesRepository;
//import com.aston.userservice.repository.UserRepository;
//import com.aston.userservice.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@IntegrationTest
//class UserServiceTest extends PostgresTestContainer {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RequisitesRepository requisitesRepository;
//
//    @BeforeEach
//    void setup() {
//        userRepository.deleteAll().block();
//        requisitesRepository.deleteAll().block();
//    }
//
//    @Test
//    void findByLogin() {
//        User user = TestConstantsUser.USER.toBuilder().id(UUID.randomUUID()).build();
//        StepVerifier.create(userRepository.save(user))
//                .expectNextMatches(savedUser -> savedUser.getLogin().equals(TestConstantsUser.LOGIN_NAME))
//                .verifyComplete();
//
//        StepVerifier.create(userService.findByLogin(TestConstantsUser.LOGIN_NAME))
//                .expectNextMatches(userProjection -> userProjection.getLogin().equals(TestConstantsUser.LOGIN_NAME))
//                .verifyComplete();
//    }
//
//    @Test
//    void getUserRequisitesById() {
//        User user = TestConstantsUser.USER.toBuilder().id(UUID.randomUUID()).build();
//        Mono<User> savedUserMono = userRepository.save(user);
//
//        StepVerifier.create(savedUserMono)
//                .assertNext(savedUser -> {
//                    assertNotNull(savedUser);
//                    Requisites requisites = TestConstantsRequisites.REQUISITES.toBuilder()
//                            .id(UUID.randomUUID())
//                            .userId(savedUser.getId())
//                            .build();
//                    StepVerifier.create(requisitesRepository.save(requisites))
//                            .assertNext(savedRequisites -> {
//                                assertNotNull(savedRequisites);
//
//                                StepVerifier.create(userService.getUserRequisitesById(savedUser.getId()))
//                                        .assertNext(response -> {
//                                            assertNotNull(response, "Ответ не должен быть пустым");
//                                            assertEquals(savedRequisites.getAccountNumber(), response.getAccountNumber(), "Неверный номер счета");
//                                            assertEquals(savedRequisites.getKbk(), response.getKbk(), "Неверный KБK");
//                                            assertEquals(savedUser.getFirstName(), response.getFirstName(), "Неверное имя пользователя");
//                                        })
//                                        .verifyComplete();
//                            })
//                            .verifyComplete();
//                })
//                .verifyComplete();
//    }
//
//    @Test
//    void getUserRequisitesByIdThrowsExceptionIfNotFound() {
//        UUID nonExistentUserId = UUID.randomUUID();
//
//        StepVerifier.create(userService.getUserRequisitesById(nonExistentUserId))
//                .expectErrorMatches(throwable -> throwable instanceof RequisitesNotFoundException &&
//                        throwable.getMessage().equals("Реквизиты счета не найдены по id пользователя: " + nonExistentUserId))
//                .verify();
//    }
//}
