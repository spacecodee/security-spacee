package com.spacecodee.securityspacee.user.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.spacecodee.securityspacee.user.domain.exception.InvalidUserDataException;
import com.spacecodee.securityspacee.user.domain.valueobject.Email;
import com.spacecodee.securityspacee.user.domain.valueobject.Password;
import com.spacecodee.securityspacee.user.domain.valueobject.UserType;
import com.spacecodee.securityspacee.user.domain.valueobject.Username;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("US-002: SYSTEM User Domain Rules")
class SystemUserDomainTest {

    @Test
    @DisplayName("SYSTEM user MUST NOT have profile")
    void whenCreateSystemUser_thenProfileMustBeNull() {
        Username username = Username.of("batch_processor");
        Email email = Email.of("system@internal.spacee");
        Password password = Password.ofHashed("$2a$10$hashedPassword");

        User user = User.create(username, email, password, UserType.SYSTEM, null);

        assertNotNull(user);
        assertEquals(UserType.SYSTEM, user.getUserType());
        assertNull(user.getProfile());
        assertEquals("batch_processor", user.getUsername().getValue());
        assertEquals("system@internal.spacee", user.getEmail().getValue());
    }

    @Test
    @DisplayName("SYSTEM user with profile throws InvalidUserDataException")
    void whenCreateSystemUserWithProfile_thenThrowException() {
        Username username = Username.of("invalid_system");
        Email email = Email.of("invalid@internal.spacee");
        Password password = Password.ofHashed("$2a$10$hashedPassword");

        UserProfileSnapshot profileSnapshot = new UserProfileSnapshot(
                "Should", "Fail", null, false, "en", null, null, null, null);
        UserProfile profile = UserProfile.of(profileSnapshot);

        InvalidUserDataException exception = assertThrows(
                InvalidUserDataException.class,
                () -> User.create(username, email, password, UserType.SYSTEM, profile));

        assertEquals("user.exception.non_human_profile_forbidden", exception.getMessage());
    }

    @Test
    @DisplayName("SERVICE user MUST NOT have profile")
    void whenCreateServiceUser_thenProfileMustBeNull() {
        Username username = Username.of("api_gateway");
        Email email = Email.of("service@api.spacee");
        Password password = Password.ofHashed("$2a$10$hashedPassword");

        User user = User.create(username, email, password, UserType.SERVICE, null);

        assertNotNull(user);
        assertEquals(UserType.SERVICE, user.getUserType());
        assertNull(user.getProfile());
    }

    @Test
    @DisplayName("SERVICE user with profile throws InvalidUserDataException")
    void whenCreateServiceUserWithProfile_thenThrowException() {
        Username username = Username.of("invalid_service");
        Email email = Email.of("invalid@api.spacee");
        Password password = Password.ofHashed("$2a$10$hashedPassword");

        UserProfileSnapshot profileSnapshot = new UserProfileSnapshot(
                "Should", "Fail", null, false, "en", null, null, null, null);
        UserProfile profile = UserProfile.of(profileSnapshot);

        InvalidUserDataException exception = assertThrows(
                InvalidUserDataException.class,
                () -> User.create(username, email, password, UserType.SERVICE, profile));

        assertEquals("user.exception.non_human_profile_forbidden", exception.getMessage());
    }

    @Test
    @DisplayName("HUMAN user MUST have profile")
    void whenCreateHumanUserWithoutProfile_thenThrowException() {
        Username username = Username.of("john_doe");
        Email email = Email.of("john@example.com");
        Password password = Password.ofHashed("$2a$10$hashedPassword");

        InvalidUserDataException exception = assertThrows(
                InvalidUserDataException.class,
                () -> User.create(username, email, password, UserType.HUMAN, null));

        assertEquals("user.exception.human_profile_required", exception.getMessage());
    }

    @Test
    @DisplayName("UserType.requiresProfile() returns correct values")
    void testRequiresProfileLogic() {
        assertTrue(UserType.HUMAN.requiresProfile());
        assertFalse(UserType.SYSTEM.requiresProfile());
        assertFalse(UserType.SERVICE.requiresProfile());
    }
}
