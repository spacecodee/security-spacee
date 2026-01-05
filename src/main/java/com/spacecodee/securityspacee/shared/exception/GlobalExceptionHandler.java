package com.spacecodee.securityspacee.shared.exception;

import java.time.Instant;
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

import com.spacecodee.securityspacee.auth.domain.exception.InvalidCredentialsException;
import com.spacecodee.securityspacee.auth.domain.exception.UserAccountLockedException;
import com.spacecodee.securityspacee.auth.domain.exception.UserNotFoundException;
import com.spacecodee.securityspacee.role.domain.exception.ParentRoleNotFoundException;
import com.spacecodee.securityspacee.role.domain.exception.RoleAlreadyExistsException;
import com.spacecodee.securityspacee.role.domain.exception.RoleHierarchyCycleException;
import com.spacecodee.securityspacee.role.domain.exception.RoleNotFoundException;
import com.spacecodee.securityspacee.role.domain.exception.SystemRoleTagAlreadyUsedException;
import com.spacecodee.securityspacee.session.domain.exception.MaxSessionsExceededException;
import com.spacecodee.securityspacee.shared.exception.base.AuthenticationException;
import com.spacecodee.securityspacee.shared.exception.base.AuthorizationException;
import com.spacecodee.securityspacee.shared.exception.base.ConflictException;
import com.spacecodee.securityspacee.shared.exception.base.ResourceNotFoundException;
import com.spacecodee.securityspacee.shared.exception.base.ValidationException;
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

    /**
     * Handler for AuthenticationException (401 UNAUTHORIZED)
     * Covers: InvalidTokenException, TokenExpiredException,
     * InvalidSignatureException,
     * TokenRevokedException, RevokedTokenException, TokenNotFoundException,
     * UserNotFoundException, InvalidCredentialsException, SessionExpiredException
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(
            @NonNull AuthenticationException ex,
            @NonNull HttpServletRequest request) {
        log.warn("AuthenticationException: {}", ex.getClass().getSimpleName());

        Locale locale = LocaleContextHolder.getLocale();
        String title = determineAuthenticationExceptionTitle(ex, locale);
        String message = messageSource.getMessage(
                ex.getMessage(),
                null,
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "authentication-error")
                .title(title)
                .status(HttpStatus.UNAUTHORIZED.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    /**
     * Handler for AuthorizationException (403 FORBIDDEN)
     * Covers: AccountInactiveException
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ProblemDetail> handleAuthorizationException(
            @NonNull AuthorizationException ex,
            @NonNull HttpServletRequest request) {
        log.warn("AuthorizationException: {}", ex.getClass().getSimpleName());

        Locale locale = LocaleContextHolder.getLocale();
        String title = messageSource.getMessage("auth.exception.account_inactive", null, locale);
        String message = messageSource.getMessage(
                ex.getMessage(),
                null,
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "authorization-error")
                .title(title)
                .status(HttpStatus.FORBIDDEN.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    /**
     * Handler for ValidationException (400 BAD_REQUEST)
     * Covers: TokenHasNotExpiredException, InvalidTokenTypeException,
     * InvalidUserDataException, SessionInvalidStateException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            @NonNull ValidationException ex,
            @NonNull HttpServletRequest request) {
        log.warn("ValidationException: {}", ex.getClass().getSimpleName());

        Locale locale = LocaleContextHolder.getLocale();
        String title = determineValidationExceptionTitle(ex, locale);

        String message;
        if (ex instanceof InvalidUserDataException invalidUserData) {
            message = messageSource.getMessage(
                    invalidUserData.getMessageKey(),
                    invalidUserData.getArgs(),
                    invalidUserData.getMessageKey(),
                    locale);
        } else {
            message = messageSource.getMessage(
                    ex.getMessage(),
                    null,
                    ex.getMessage(),
                    locale);
        }

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "validation-error")
                .title(title)
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    /**
     * Handler for ResourceNotFoundException (404 NOT_FOUND)
     * Covers: SessionNotFoundException, ParentRoleNotFoundException,
     * RoleNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            @NonNull ResourceNotFoundException ex,
            @NonNull HttpServletRequest request) {
        log.warn("ResourceNotFoundException: {}", ex.getClass().getSimpleName());

        Locale locale = LocaleContextHolder.getLocale();
        String title = determineResourceNotFoundExceptionTitle(ex, locale);
        String message = messageSource.getMessage(
                ex.getMessage(),
                null,
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "not-found")
                .title(title)
                .status(HttpStatus.NOT_FOUND.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    /**
     * Handler for ConflictException (409 CONFLICT)
     * Covers: TokenAlreadyRevokedException, DuplicateUsernameException,
     * DuplicateEmailException, RoleAlreadyExistsException,
     * SystemRoleTagAlreadyUsedException
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflictException(
            @NonNull ConflictException ex,
            @NonNull HttpServletRequest request) {
        log.warn("ConflictException: {}", ex.getClass().getSimpleName());

        Locale locale = LocaleContextHolder.getLocale();
        String title = determineConflictExceptionTitle(ex, locale);

        String message = switch (ex) {
            case DuplicateUsernameException dup -> messageSource.getMessage(
                    ex.getMessage(),
                    new Object[]{dup.getUsername()},
                    ex.getMessage(),
                    locale);
            case DuplicateEmailException dup -> messageSource.getMessage(
                    ex.getMessage(),
                    new Object[]{dup.getEmail()},
                    ex.getMessage(),
                    locale);
            case RoleAlreadyExistsException dup -> messageSource.getMessage(
                    ex.getMessage(),
                    new Object[]{dup.getRoleName()},
                    ex.getMessage(),
                    locale);
            case SystemRoleTagAlreadyUsedException dup -> messageSource.getMessage(
                    ex.getMessage(),
                    new Object[]{dup.getSystemRoleTag()},
                    ex.getMessage(),
                    locale);
            default -> messageSource.getMessage(
                    ex.getMessage(),
                    null,
                    ex.getMessage(),
                    locale);
        };

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "conflict")
                .title(title)
                .status(HttpStatus.CONFLICT.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    /**
     * Handler for AccountLockedException (423 LOCKED)
     * Special case: Requires lockedUntil timestamp
     */
    @ExceptionHandler(UserAccountLockedException.class)
    public ResponseEntity<ProblemDetail> handleAccountLocked(
            @NonNull UserAccountLockedException ex,
            @NonNull HttpServletRequest request) {
        log.warn("UserAccountLockedException: Account locked until {}", ex.getLockedUntil());

        Locale locale = LocaleContextHolder.getLocale();
        Instant lockedUntil = ex.getLockedUntil();

        String message = messageSource.getMessage(
                ex.getMessage(),
                null,
                ex.getMessage(),
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "account-locked")
                .title(messageSource.getMessage("auth.exception.account_locked",
                        new Object[]{lockedUntil.toString()}, locale))
                .status(HttpStatus.LOCKED.value())
                .detail(message)
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.LOCKED).body(problem);
    }

    /**
     * Handler for MaxSessionsExceededException (429 TOO MANY REQUESTS)
     */
    @ExceptionHandler(MaxSessionsExceededException.class)
    public ResponseEntity<ProblemDetail> handleMaxSessionsExceededException(
            @NonNull MaxSessionsExceededException ex,
            @NonNull HttpServletRequest request) {
        log.warn("MaxSessionsExceededException: {}", ex.getMessage());

        Locale locale = LocaleContextHolder.getLocale();
        String title = messageSource.getMessage("session.exception.max_sessions_exceeded_title",
                null,
                "Maximum Sessions Exceeded",
                locale);

        ProblemDetail problem = ProblemDetail.builder()
                .type(PROBLEM_BASE_URI + "max-sessions-exceeded")
                .title(title)
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(problem);
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

    // Helper methods for determining titles based on specific exception types

    private @NonNull String determineAuthenticationExceptionTitle(
            AuthenticationException ex,
            Locale locale) {
        if (ex instanceof UserNotFoundException) {
            return messageSource.getMessage("auth.exception.user_not_found", null, locale);
        } else if (ex instanceof InvalidCredentialsException) {
            return messageSource.getMessage("auth.exception.invalid_credentials", null, locale);
        }
        return messageSource.getMessage("error.authentication.title", null, locale);
    }

    private @NonNull String determineValidationExceptionTitle(
            ValidationException ex,
            Locale locale) {
        if (ex instanceof InvalidUserDataException) {
            return messageSource.getMessage("user.exception.invalid_user_data", null, locale);
        } else if (ex instanceof RoleHierarchyCycleException) {
            return messageSource.getMessage("role.error.hierarchy_cycle", null, locale);
        }
        return messageSource.getMessage("validation.error.title", null, locale);
    }

    private @NonNull String determineConflictExceptionTitle(
            ConflictException ex,
            Locale locale) {
        if (ex instanceof DuplicateUsernameException) {
            return messageSource.getMessage("user.exception.duplicate_username", null, locale);
        } else if (ex instanceof DuplicateEmailException) {
            return messageSource.getMessage("user.exception.duplicate_email", null, locale);
        } else if (ex instanceof RoleAlreadyExistsException) {
            return messageSource.getMessage("role.exception.role_already_exists", null, locale);
        } else if (ex instanceof SystemRoleTagAlreadyUsedException) {
            return messageSource.getMessage("role.exception.system_role_tag_already_used", null, locale);
        }
        return messageSource.getMessage("conflict.error.title", null, locale);
    }

    private @NonNull String determineResourceNotFoundExceptionTitle(
            ResourceNotFoundException ex,
            Locale locale) {
        if (ex instanceof ParentRoleNotFoundException || ex instanceof RoleNotFoundException) {
            return messageSource.getMessage("role.error.not_found", null, locale);
        }
        return messageSource.getMessage("session.error.not_found", null, locale);
    }
}
