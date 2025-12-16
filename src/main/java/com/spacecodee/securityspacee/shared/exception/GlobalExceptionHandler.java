package com.spacecodee.securityspacee.shared.exception;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.spacecodee.securityspacee.auth.domain.exception.AccountInactiveException;
import com.spacecodee.securityspacee.auth.domain.exception.AccountLockedException;
import com.spacecodee.securityspacee.auth.domain.exception.InvalidCredentialsException;
import com.spacecodee.securityspacee.auth.domain.exception.UserNotFoundException;
import com.spacecodee.securityspacee.user.domain.exception.DuplicateEmailException;
import com.spacecodee.securityspacee.user.domain.exception.DuplicateUsernameException;
import com.spacecodee.securityspacee.user.domain.exception.InvalidUserDataException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_BASE_URI = "https://api.spacee.com/problems/";

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateUsername(
            @NonNull DuplicateUsernameException ex,
            @NonNull HttpServletRequest request) {
        log.warn("DuplicateUsernameException: {}", ex.getUsername());

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(
                ex.getMessage(),
                new Object[]{ex.getUsername()},
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "duplicate-username")
                .title(messageSource.getMessage("user.exception.duplicate_username", null, locale))
                .status(HttpStatus.CONFLICT.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateEmail(
            @NonNull DuplicateEmailException ex,
            @NonNull HttpServletRequest request) {
        log.warn("DuplicateEmailException: {}", ex.getEmail());

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(
                ex.getMessage(),
                new Object[]{ex.getEmail()},
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "duplicate-email")
                .title(messageSource.getMessage("user.exception.duplicate_email", null, locale))
                .status(HttpStatus.CONFLICT.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ProblemDetail> handleInvalidUserData(
            @NonNull InvalidUserDataException ex,
            @NonNull HttpServletRequest request) {
        log.warn("InvalidUserDataException: {}", ex.getMessageKey());

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(
                ex.getMessageKey(),
                ex.getArgs(),
                ex.getMessageKey(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "invalid-user-data")
                .title(messageSource.getMessage("user.exception.invalid_user_data", null, locale))
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(
            @NonNull MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Locale locale = LocaleContextHolder.getLocale();

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "validation-error")
                .title(messageSource.getMessage("validation.error.title", null, locale))
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(messageSource.getMessage("validation.error.detail", null, locale))
                .instance(request.getRequestURI())
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(
            @NonNull IllegalArgumentException ex,
            @NonNull HttpServletRequest request) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(
                ex.getMessage(),
                null,
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "invalid-argument")
                .title(messageSource.getMessage("validation.error.invalid_argument", null, locale))
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFound(
            @NonNull UserNotFoundException ex,
            @NonNull HttpServletRequest request) {
        log.warn("UserNotFoundException: {}", ex.getMessage());

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(
                ex.getMessage(),
                null,
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "user-not-found")
                .title(messageSource.getMessage("auth.exception.user_not_found", null, locale))
                .status(HttpStatus.UNAUTHORIZED.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(
            @NonNull InvalidCredentialsException ex,
            @NonNull HttpServletRequest request) {
        log.warn("InvalidCredentialsException: {}", ex.getMessage());

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(
                ex.getMessage(),
                null,
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "invalid-credentials")
                .title(messageSource.getMessage("auth.exception.invalid_credentials", null, locale))
                .status(HttpStatus.UNAUTHORIZED.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ProblemDetail> handleAccountInactive(
            @NonNull AccountInactiveException ex,
            @NonNull HttpServletRequest request) {
        log.warn("AccountInactiveException: {}", ex.getMessage());

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(
                ex.getMessage(),
                null,
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "account-inactive")
                .title(messageSource.getMessage("auth.exception.account_inactive", null, locale))
                .status(HttpStatus.FORBIDDEN.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ProblemDetail> handleAccountLocked(
            @NonNull AccountLockedException ex,
            @NonNull HttpServletRequest request) {
        log.warn("AccountLockedException: Account locked until {}", ex.getLockedUntil());

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(
                ex.getMessage(),
                null,
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "account-locked")
                .title(messageSource.getMessage("auth.exception.account_locked",
                        new Object[]{ex.getLockedUntil().toString()}, locale))
                .status(HttpStatus.LOCKED.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.LOCKED).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex,
            @NonNull HttpServletRequest request) {
        log.error("Unhandled exception: ", ex);

        Locale locale = LocaleContextHolder.getLocale();

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "internal-error")
                .title(messageSource.getMessage("error.internal.title", null, locale))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail(messageSource.getMessage("error.internal.detail", null, locale))
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
