package com.spacecodee.securityspacee.jwttoken.application.usecase;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;

import com.spacecodee.securityspacee.jwttoken.application.command.ValidateTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.mapper.ITokenValidationResponseMapper;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IValidateTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IJwtCryptoService;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenValidationResponse;
import com.spacecodee.securityspacee.jwttoken.domain.event.OrphanedTokenDetectedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.event.TokenValidationFailedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.exception.InvalidSignatureException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.InvalidTokenException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenExpiredException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenRevokedException;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.repository.IJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Claims;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.ValidationFailureReason;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.ValidationMode;
import com.spacecodee.securityspacee.session.domain.event.SessionExpiredEvent;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;
import com.spacecodee.securityspacee.shared.application.port.out.IMessageResolverPort;

public final class ValidateTokenUseCase implements IValidateTokenUseCase {

    private final IJwtTokenRepository jwtTokenRepository;
    private final ISessionRepository sessionRepository;
    private final IJwtCryptoService jwtCryptoService;
    private final IClockService clockService;
    private final ApplicationEventPublisher eventPublisher;
    private final IMessageResolverPort messageResolverPort;
    private final ITokenValidationResponseMapper validationResponseMapper;

    public ValidateTokenUseCase(
            IJwtTokenRepository jwtTokenRepository,
            ISessionRepository sessionRepository,
            IJwtCryptoService jwtCryptoService,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher,
            IMessageResolverPort messageResolverPort,
            ITokenValidationResponseMapper validationResponseMapper) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.sessionRepository = sessionRepository;
        this.jwtCryptoService = jwtCryptoService;
        this.clockService = clockService;
        this.eventPublisher = eventPublisher;
        this.messageResolverPort = messageResolverPort;
        this.validationResponseMapper = validationResponseMapper;
    }

    @Override
    public @NonNull TokenValidationResponse execute(@NonNull ValidateTokenCommand command) {
        if (!this.jwtCryptoService.validateSignature(command.token())) {
            this.publishValidationFailedEvent(command.token(), ValidationFailureReason.INVALID_SIGNATURE);
            throw new InvalidSignatureException(this.getMessage("jwttoken.exception.invalid_signature"));
        }

        Map<String, Object> claimsMap = this.jwtCryptoService.parseToken(command.token());
        Claims claims = Claims.fromMap(claimsMap);

        Instant expiry = this.jwtCryptoService.getExpirationDate(command.token());
        if (this.clockService.now().isAfter(expiry)) {
            this.publishValidationFailedEvent(command.token(), ValidationFailureReason.EXPIRED);
            throw new TokenExpiredException(
                    this.getMessage("jwttoken.exception.token_expired"),
                    expiry);
        }

        if (command.mode() == ValidationMode.STRICT) {
            return this.strictValidation(claims, expiry, command.token());
        }

        return this.validationResponseMapper.toResponse(claims, expiry);
    }

    @Contract("_, _, _ -> new")
    private @NonNull TokenValidationResponse strictValidation(@NonNull Claims claims, Instant expiry, String token) {
        Instant now = this.clockService.now();
        Jti jti = Jti.parse(claims.getJti());

        JwtToken jwtToken = this.jwtTokenRepository.findByJti(jti)
                .orElseThrow(() -> {
                    this.publishValidationFailedEvent(token, ValidationFailureReason.REVOKED);
                    return new InvalidTokenException(this.getMessage("jwttoken.exception.token_not_registered"));
                });

        if (jwtToken.getState() == TokenState.REVOKED) {
            this.publishValidationFailedEvent(token, ValidationFailureReason.REVOKED);
            throw new TokenRevokedException(
                    this.getMessage("jwttoken.exception.token_revoked"),
                    jwtToken.getJti().toString(),
                    jwtToken.getRevocationInfo().getRevokedAt(),
                    jwtToken.getRevocationInfo().getReason());
        }

        if (jwtToken.getState() == TokenState.BLACKLISTED) {
            this.publishValidationFailedEvent(token, ValidationFailureReason.BLACKLISTED);
            throw new InvalidTokenException(this.getMessage("jwttoken.exception.token_blacklisted"));
        }

        if (jwtToken.getExpiryDate().isBefore(now)) {
            JwtToken expiredToken = jwtToken.markAsExpired();
            this.jwtTokenRepository.save(expiredToken);
            this.publishValidationFailedEvent(token, ValidationFailureReason.EXPIRED);
            throw new TokenExpiredException(
                    this.getMessage("jwttoken.exception.token_expired_db"),
                    jwtToken.getExpiryDate());
        }

        SessionId sessionId = SessionId.parse(claims.getSessionId());
        Optional<Session> sessionOpt = this.sessionRepository.findById(sessionId);

        if (sessionOpt.isEmpty()) {
            this.handleOrphanedToken(jwtToken, now);
            throw new InvalidTokenException(this.getMessage("jwttoken.exception.session_not_found"));
        }

        Session session = sessionOpt.get();

        if (!session.isActive()) {
            this.publishValidationFailedEvent(token, ValidationFailureReason.SESSION_INACTIVE);
            throw new InvalidTokenException(
                    this.getMessage("jwttoken.exception.session_inactive", session.getState()));
        }

        if (session.getMetadata().getExpiresAt().isBefore(now)) {
            Session expiredSession = session.expire();
            this.sessionRepository.save(expiredSession);

            this.eventPublisher.publishEvent(SessionExpiredEvent.builder()
                    .sessionId(sessionId)
                    .userId(session.getUserId())
                    .expiredAt(now)
                    .build());

            this.publishValidationFailedEvent(token, ValidationFailureReason.SESSION_EXPIRED);
            throw new InvalidTokenException(
                    this.getMessage("jwttoken.exception.session_expired", session.getMetadata().getExpiresAt()));
        }

        JwtToken updatedToken = jwtToken.incrementUsage(now);
        this.jwtTokenRepository.save(updatedToken);

        this.updateSessionActivity(sessionId);

        return this.validationResponseMapper.toResponse(updatedToken, claims, expiry);
    }

    private void updateSessionActivity(@NonNull SessionId sessionId) {
        this.sessionRepository.findById(sessionId)
                .filter(Session::isActive)
                .ifPresent(session -> {
                    Session updatedSession = session.updateActivity();
                    this.sessionRepository.save(updatedSession);
                });
    }

    private void handleOrphanedToken(@NonNull JwtToken jwtToken, @NonNull Instant now) {
        JwtToken revokedToken = jwtToken.revoke(null, "session_deleted", now);
        this.jwtTokenRepository.save(revokedToken);

        OrphanedTokenDetectedEvent event = OrphanedTokenDetectedEvent.builder()
                .jti(jwtToken.getJti().toString())
                .userId(jwtToken.getUserId())
                .sessionId(jwtToken.getSessionId())
                .detectedAt(now)
                .build();

        this.eventPublisher.publishEvent(event);
        this.publishValidationFailedEvent(jwtToken.getRawToken(), ValidationFailureReason.SESSION_NOT_FOUND);
    }

    private void publishValidationFailedEvent(@NonNull String token, ValidationFailureReason reason) {
        String lastChars = token.length() > 10 ? token.substring(token.length() - 10) : token;

        TokenValidationFailedEvent event = TokenValidationFailedEvent.builder()
                .tokenLastChars(lastChars)
                .reason(reason)
                .attemptedAt(this.clockService.now())
                .ipAddress("unknown")
                .build();

        this.eventPublisher.publishEvent(event);
    }

    private @NonNull String getMessage(String code, Object... args) {
        return this.messageResolverPort.getMessage(code, args);
    }
}
