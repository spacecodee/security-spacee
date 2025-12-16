package com.spacecodee.securityspacee.user.adapter.controller.impl;

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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("US-002: Register SYSTEM User Integration Tests")
class RegisterSystemUserIntegrationTest {

    @Autowired
    private IRegisterUserUseCase registerUserUseCase;

    @Test
    @DisplayName("Scenario 1: Successfully create SYSTEM user without profile")
    void whenRegisterSystemUser_thenCreateWithoutProfile() {
        RegisterUserCommand command = new RegisterUserCommand(
                "batch_processor",
                "system@internal.spacee",
                "SecureP@ssw0rd123",
                UserType.SYSTEM,
                null, null, null, "en", null, null, null, null);

        UserResponse response = registerUserUseCase.register(command);

        assertNotNull(response);
        assertNotNull(response.userId());
        assertEquals("batch_processor", response.username());
        assertEquals("system@internal.spacee", response.email());
        assertEquals(UserType.SYSTEM, response.userType());
        assertTrue(response.isActive());
        assertFalse(response.emailVerified());
    }

    @Test
    @DisplayName("Scenario 2: SYSTEM user ignores profile fields in request")
    void whenRegisterSystemUserWithProfileData_thenIgnoreProfileFields() {
        RegisterUserCommand command = new RegisterUserCommand(
                "automation_service",
                "automation@internal.spacee",
                "Automation@Pass123",
                UserType.SYSTEM,
                "Should", "BeIgnored", "+1234567890",
                "es", "https://avatar.url/ignored.png",
                "This bio should be ignored", "America/New_York", null);

        UserResponse response = registerUserUseCase.register(command);

        assertNotNull(response);
        assertEquals("automation_service", response.username());
        assertEquals(UserType.SYSTEM, response.userType());
    }

    @Test
    @DisplayName("Scenario 3: SYSTEM user with duplicate username fails")
    void whenRegisterSystemUserWithDuplicateUsername_thenFail() {
        RegisterUserCommand firstCommand = new RegisterUserCommand(
                "unique_system", "first@internal.spacee", "FirstPass@123",
                UserType.SYSTEM, null, null, null, "en", null, null, null, null);

        registerUserUseCase.register(firstCommand);

        RegisterUserCommand duplicateCommand = new RegisterUserCommand(
                "unique_system", "second@internal.spacee", "SecondPass@123",
                UserType.SYSTEM, null, null, null, "en", null, null, null, null);

        assertThrows(DuplicateUsernameException.class,
                () -> registerUserUseCase.register(duplicateCommand));
    }

    @Test
    @DisplayName("Scenario 4: SYSTEM user with duplicate email fails")
    void whenRegisterSystemUserWithDuplicateEmail_thenFail() {
        RegisterUserCommand firstCommand = new RegisterUserCommand(
                "system_one", "shared@internal.spacee", "FirstPass@123",
                UserType.SYSTEM, null, null, null, "en", null, null, null, null);

        registerUserUseCase.register(firstCommand);

        RegisterUserCommand duplicateCommand = new RegisterUserCommand(
                "system_two", "shared@internal.spacee", "SecondPass@123",
                UserType.SYSTEM, null, null, null, "en", null, null, null, null);

        assertThrows(DuplicateEmailException.class,
                () -> registerUserUseCase.register(duplicateCommand));
    }
}
