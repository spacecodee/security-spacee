package com.spacecodee.securityspacee.shared.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Internationalization Configuration Tests")
class InternationalizationConfigTest {

    @Autowired
    private MessageSource messageSource;

    @Test
    @DisplayName("MessageSource bean is loaded and configured")
    void whenMessageSourceIsInjected_thenNotNull() {
        assertNotNull(messageSource);
    }

    @Test
    @DisplayName("English messages are resolved correctly")
    void whenGetEnglishMessage_thenReturnsCorrectTranslation() {
        String message = messageSource.getMessage(
                "user.register.success",
                null,
                Locale.ENGLISH);

        assertEquals("User registered successfully", message);
    }

    @Test
    @DisplayName("Spanish messages are resolved correctly")
    void whenGetSpanishMessage_thenReturnsCorrectTranslation() {
        String message = messageSource.getMessage(
                "user.register.success",
                null,
                Locale.forLanguageTag("es"));

        assertEquals("Usuario registrado exitosamente", message);
    }

    @Test
    @DisplayName("Parameterized messages work with placeholders")
    void whenGetMessageWithParameters_thenReplacesPlaceholders() {
        String message = messageSource.getMessage(
                "user.exception.duplicate_username",
                new Object[]{"john_doe"},
                Locale.ENGLISH);

        assertEquals("Username 'john_doe' already exists in the system", message);
    }

    @Test
    @DisplayName("Fallback to English when message not found in Spanish")
    void whenMessageNotFoundInLocale_thenFallbackToDefault() {
        String message = messageSource.getMessage(
                "user.email.welcome.subject",
                null,
                Locale.ENGLISH);

        assertEquals("Welcome to Security Spacee!", message);
    }

    @Test
    @DisplayName("Validation messages are loaded")
    void whenGetValidationMessage_thenReturnsCorrectMessage() {
        String message = messageSource.getMessage(
                "user.validation.username.required",
                null,
                Locale.ENGLISH);

        assertEquals("Username is required", message);
    }

    @Test
    @DisplayName("Nested messages with multiple levels work")
    void whenGetNestedMessage_thenReturnsCorrectMessage() {
        String message = messageSource.getMessage(
                "user.email.password_reset.subject",
                null,
                Locale.ENGLISH);

        assertEquals("Password Reset Request", message);
    }
}
