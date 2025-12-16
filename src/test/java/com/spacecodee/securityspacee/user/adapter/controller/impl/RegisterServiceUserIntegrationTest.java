package com.spacecodee.securityspacee.user.adapter.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.user.application.command.RegisterUserCommand;
import com.spacecodee.securityspacee.user.application.port.in.IRegisterUserUseCase;
import com.spacecodee.securityspacee.user.application.response.UserResponse;
import com.spacecodee.securityspacee.user.domain.exception.DuplicateEmailException;
import com.spacecodee.securityspacee.user.domain.exception.DuplicateUsernameException;
import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("US-003: Register SERVICE User Integration Tests")
class RegisterServiceUserIntegrationTest {

    @Autowired
    private IRegisterUserUseCase registerUserUseCase;

    @Test
    @DisplayName("Scenario 1: Successfully create SERVICE user without profile")
    void whenRegisterServiceUser_thenCreateWithoutProfile() {
        RegisterUserCommand command = new RegisterUserCommand(
                "payment_gateway_api",
                "payment-svc@spacee.com",
                "ApiKey@SecureP@ss123",
                UserType.SERVICE,
                null, null, null, "en", null, null, null, null);

        UserResponse response = registerUserUseCase.register(command);

        assertNotNull(response);
        assertNotNull(response.userId());
        assertEquals("payment_gateway_api", response.username());
        assertEquals("payment-svc@spacee.com", response.email());
        assertEquals(UserType.SERVICE, response.userType());
        assertTrue(response.isActive());
        assertFalse(response.emailVerified());
    }

    @Test
    @DisplayName("Scenario 2: SERVICE user ignores profile fields in request")
    void whenRegisterServiceUserWithProfileData_thenIgnoreProfileFields() {
        RegisterUserCommand command = new RegisterUserCommand(
                "notification_service",
                "notification-svc@spacee.com",
                "NotifApi@Key456",
                UserType.SERVICE,
                "Should", "BeIgnored", "+9876543210",
                "es", "https://avatar.url/service.png",
                "This service bio is ignored", "UTC", null);

        UserResponse response = registerUserUseCase.register(command);

        assertNotNull(response);
        assertEquals("notification_service", response.username());
        assertEquals(UserType.SERVICE, response.userType());
    }

    @Test
    @DisplayName("Scenario 3: SERVICE user with duplicate username fails")
    void whenRegisterServiceUserWithDuplicateUsername_thenFail() {
        RegisterUserCommand firstCommand = new RegisterUserCommand(
                "auth_service_api", "auth-svc@spacee.com", "AuthApi@Key789",
                UserType.SERVICE, null, null, null, "en", null, null, null, null);

        registerUserUseCase.register(firstCommand);

        RegisterUserCommand duplicateCommand = new RegisterUserCommand(
                "auth_service_api", "auth-svc-duplicate@spacee.com", "DupeApi@Key999",
                UserType.SERVICE, null, null, null, "en", null, null, null, null);

        assertThrows(DuplicateUsernameException.class,
                () -> registerUserUseCase.register(duplicateCommand));
    }

    @Test
    @DisplayName("Scenario 4: SERVICE user with duplicate email fails")
    void whenRegisterServiceUserWithDuplicateEmail_thenFail() {
        RegisterUserCommand firstCommand = new RegisterUserCommand(
                "inventory_service", "inventory-svc@spacee.com", "InvApi@Key111",
                UserType.SERVICE, null, null, null, "en", null, null, null, null);

        registerUserUseCase.register(firstCommand);

        RegisterUserCommand duplicateCommand = new RegisterUserCommand(
                "warehouse_service", "inventory-svc@spacee.com", "WareApi@Key222",
                UserType.SERVICE, null, null, null, "en", null, null, null, null);

        assertThrows(DuplicateEmailException.class,
                () -> registerUserUseCase.register(duplicateCommand));
    }
}
