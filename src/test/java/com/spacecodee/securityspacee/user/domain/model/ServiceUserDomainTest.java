package com.spacecodee.securityspacee.user.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.spacecodee.securityspacee.user.domain.exception.InvalidUserDataException;
import com.spacecodee.securityspacee.user.domain.valueobject.Email;
import com.spacecodee.securityspacee.user.domain.valueobject.Password;
import com.spacecodee.securityspacee.user.domain.valueobject.UserType;
import com.spacecodee.securityspacee.user.domain.valueobject.Username;

@DisplayName("US-003: SERVICE User Domain Rules")
class ServiceUserDomainTest {

    @Test
    @DisplayName("SERVICE user with null profile succeeds")
    void whenCreateServiceUser_thenProfileMustBeNull() {
        Username username = Username.of("payment_gateway_api");
        Email email = Email.of("payment-svc@spacee.com");
        Password password = Password.ofHashed("$2a$10$ApiKeyHashedAsPassword");

        User user = User.create(username, email, password, UserType.SERVICE, null);

        assertNotNull(user);
        assertEquals(UserType.SERVICE, user.getUserType());
        assertNull(user.getProfile());
        assertEquals("payment_gateway_api", user.getUsername().getValue());
        assertEquals("payment-svc@spacee.com", user.getEmail().getValue());
    }

    @Test
    @DisplayName("SERVICE user with profile throws InvalidUserDataException")
    void whenCreateServiceUserWithProfile_thenThrowException() {
        Username username = Username.of("invalid_service_with_profile");
        Email email = Email.of("invalid-svc@spacee.com");
        Password password = Password.ofHashed("$2a$10$ApiKeyHashedAsPassword");

        UserProfileSnapshot profileSnapshot = new UserProfileSnapshot(
                "API", "Service", null, false, "en", null, null, null, null);
        UserProfile profile = UserProfile.of(profileSnapshot);

        InvalidUserDataException exception = assertThrows(
                InvalidUserDataException.class,
                () -> User.create(username, email, password, UserType.SERVICE, profile));

        assertEquals("user.exception.non_human_profile_forbidden", exception.getMessage());
    }

    @Test
    @DisplayName("UserType.requiresProfile() returns false for SERVICE")
    void testServiceUserTypeDoesNotRequireProfile() {
        assertFalse(UserType.SERVICE.requiresProfile());
    }
}
