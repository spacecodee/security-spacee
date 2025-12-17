package com.spacecodee.securityspacee.auth.application.usecase;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.spacecodee.securityspacee.auth.application.command.LoginCommand;
import com.spacecodee.securityspacee.auth.application.mapper.IAuthenticationResponseMapper;
import com.spacecodee.securityspacee.auth.application.port.in.ILoginUseCase;
import com.spacecodee.securityspacee.auth.application.port.out.AuthCreateSessionCommand;
import com.spacecodee.securityspacee.auth.application.port.out.AuthIssueTokenCommand;
import com.spacecodee.securityspacee.auth.application.port.out.IPasswordValidator;
import com.spacecodee.securityspacee.auth.application.port.out.ISessionService;
import com.spacecodee.securityspacee.auth.application.port.out.ITokenService;
import com.spacecodee.securityspacee.auth.application.port.out.IUserAuthenticationPort;
import com.spacecodee.securityspacee.auth.application.port.out.TokenPair;
import com.spacecodee.securityspacee.auth.application.response.AuthenticationResponse;
import com.spacecodee.securityspacee.auth.domain.event.AccountLockedEvent;
import com.spacecodee.securityspacee.auth.domain.event.LoginFailedEvent;
import com.spacecodee.securityspacee.auth.domain.event.LoginSuccessEvent;
import com.spacecodee.securityspacee.auth.domain.exception.AccountInactiveException;
import com.spacecodee.securityspacee.auth.domain.exception.AccountLockedException;
import com.spacecodee.securityspacee.auth.domain.exception.InvalidCredentialsException;
import com.spacecodee.securityspacee.auth.domain.exception.UserNotFoundException;
import com.spacecodee.securityspacee.auth.domain.valueobject.AccountLockDuration;
import com.spacecodee.securityspacee.auth.domain.valueobject.AuthenticationResult;
import com.spacecodee.securityspacee.auth.domain.valueobject.FailureReason;
import com.spacecodee.securityspacee.shared.config.properties.SecurityProperties;
import com.spacecodee.securityspacee.user.domain.model.User;

public final class LoginUseCase implements ILoginUseCase {

    private final IUserAuthenticationPort userAuthenticationPort;
    private final IPasswordValidator passwordValidator;
    private final ITokenService tokenService;
    private final ISessionService sessionService;
    private final ApplicationEventPublisher eventPublisher;
    private final IAuthenticationResponseMapper responseMapper;
    private final MessageSource messageSource;
    private final SecurityProperties securityProperties;

    @SuppressWarnings("java:S107")
    public LoginUseCase(
            IUserAuthenticationPort userAuthenticationPort,
            IPasswordValidator passwordValidator,
            ITokenService tokenService,
            ISessionService sessionService,
            ApplicationEventPublisher eventPublisher,
            IAuthenticationResponseMapper responseMapper,
            MessageSource messageSource,
            SecurityProperties securityProperties) {
        this.userAuthenticationPort = userAuthenticationPort;
        this.passwordValidator = passwordValidator;
        this.tokenService = tokenService;
        this.sessionService = sessionService;
        this.eventPublisher = eventPublisher;
        this.responseMapper = responseMapper;
        this.messageSource = messageSource;
        this.securityProperties = securityProperties;
    }

    @Override
    public AuthenticationResponse execute(@NonNull LoginCommand command) {
        User user = findUserOrFail(command.usernameOrEmail());

        validateAccountStatus(user);

        validatePasswordOrHandleFailure(command, user);

        resetFailedAttempts(user);

        updateLastLogin(user);

        AuthenticationResult authResult = buildAuthenticationResult(user);

        publishLoginSuccessEvent(authResult, command);

        TokenPair tokenPair = issueTokens(authResult);

        createSession(user.getUserId(), command);

        return responseMapper.toResponse(authResult, tokenPair);
    }

    private User findUserOrFail(String usernameOrEmail) {
        return userAuthenticationPort.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        getMessage("auth.exception.user_not_found")));
    }

    private void validateAccountStatus(@NonNull User user) {
        if (!user.isActive()) {
            throw new AccountInactiveException(
                    getMessage("auth.exception.account_inactive"));
        }

        if (user.isLocked()) {
            throw new AccountLockedException(
                    getMessage("auth.exception.account_locked", user.getLockedUntil().toString()),
                    user.getLockedUntil());
        }
    }

    private void validatePasswordOrHandleFailure(@NonNull LoginCommand command, @NonNull User user) {
        if (!passwordValidator.matches(command.password(), user.getPassword().getValue())) {
            handleFailedLogin(command, user);
            throw new InvalidCredentialsException(
                    getMessage("auth.exception.invalid_credentials"));
        }
    }

    private void handleFailedLogin(LoginCommand command, @NonNull User user) {
        userAuthenticationPort.incrementFailedAttempts(user.getUserId());

        int newAttempts = user.getFailedLoginAttempts() + 1;

        if (newAttempts >= securityProperties.maxLoginAttempts()) {
            AccountLockDuration lockDuration = AccountLockDuration.standard(securityProperties.accountLockDuration());
            userAuthenticationPort.lockAccount(user.getUserId(), lockDuration.getLockedUntil());

            publishAccountLockedEvent(user, lockDuration.getLockedUntil());
        }

        publishLoginFailedEvent(command.usernameOrEmail(), FailureReason.INVALID_CREDENTIALS,
                newAttempts, command.ipAddress());
    }

    private void resetFailedAttempts(@NonNull User user) {
        if (user.getFailedLoginAttempts() > 0) {
            userAuthenticationPort.resetFailedAttempts(user.getUserId());
        }
    }

    private void updateLastLogin(@NonNull User user) {
        userAuthenticationPort.updateLastLogin(user.getUserId(), Instant.now());
    }

    @Contract("_ -> new")
    private @NonNull AuthenticationResult buildAuthenticationResult(@NonNull User user) {
        return AuthenticationResult.builder()
                .userId(user.getUserId())
                .username(user.getUsername().getValue())
                .email(user.getEmail().getValue())
                .userType(user.getUserType())
                .isAccountActive(user.isActive())
                .isEmailVerified(user.isEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .assignedRoles(List.of())
                .build();
    }

    private void publishLoginSuccessEvent(@NonNull AuthenticationResult authResult, @NonNull LoginCommand command) {
        LoginSuccessEvent event = LoginSuccessEvent.builder()
                .userId(authResult.getUserId())
                .username(authResult.getUsername())
                .email(authResult.getEmail())
                .userType(authResult.getUserType())
                .loginTimestamp(Instant.now())
                .ipAddress(command.ipAddress())
                .userAgent(command.userAgent())
                .assignedRoles(authResult.getAssignedRoles())
                .build();

        eventPublisher.publishEvent(event);
    }

    private void publishLoginFailedEvent(String usernameOrEmail, FailureReason reason,
                                         int failedAttempts, String ipAddress) {
        LoginFailedEvent event = new LoginFailedEvent(
                usernameOrEmail, reason, failedAttempts, ipAddress, Instant.now());

        eventPublisher.publishEvent(event);
    }

    private void publishAccountLockedEvent(@NonNull User user, Instant lockedUntil) {
        AccountLockedEvent event = new AccountLockedEvent(
                user.getUserId(),
                user.getUsername().getValue(),
                user.getEmail().getValue(),
                lockedUntil,
                getMessage("auth.exception.account_locked_reason"),
                Instant.now());

        eventPublisher.publishEvent(event);
    }

    private TokenPair issueTokens(AuthenticationResult authResult) {
        AuthIssueTokenCommand tokenCommand = new AuthIssueTokenCommand(
                authResult.getUserId(),
                authResult.getUsername(),
                authResult.getEmail(),
                authResult.getUserType(),
                authResult.getAssignedRoles());

        return tokenService.issueTokens(tokenCommand);
    }

    private void createSession(Integer userId, @NonNull LoginCommand command) {
        AuthCreateSessionCommand sessionCommand = new AuthCreateSessionCommand(
                userId,
                command.ipAddress(),
                command.userAgent(),
                Instant.now());

        sessionService.createSession(sessionCommand);
    }

    private String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, code, locale);
    }
}
