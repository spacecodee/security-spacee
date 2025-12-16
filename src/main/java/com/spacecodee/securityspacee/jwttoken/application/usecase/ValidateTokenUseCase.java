package com.spacecodee.securityspacee.jwttoken.application.usecase;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.spacecodee.securityspacee.jwttoken.application.command.ValidateTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.mapper.ITokenValidationResponseMapper;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IValidateTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IJwtCryptoService;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenValidationResponse;
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

public final class ValidateTokenUseCase implements IValidateTokenUseCase {

    private final IJwtTokenRepository jwtTokenRepository;
    private final IJwtCryptoService jwtCryptoService;
    private final IClockService clockService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;
    private final ITokenValidationResponseMapper validationResponseMapper;

    public ValidateTokenUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IJwtCryptoService jwtCryptoService,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher,
            MessageSource messageSource,
            ITokenValidationResponseMapper validationResponseMapper) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.jwtCryptoService = jwtCryptoService;
        this.clockService = clockService;
        this.eventPublisher = eventPublisher;
        this.messageSource = messageSource;
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
        Jti jti = Jti.parse(claims.getJti());
        JwtToken jwtToken = this.jwtTokenRepository.findByJti(jti)
                .orElseThrow(() -> {
                    this.publishValidationFailedEvent(token, ValidationFailureReason.REVOKED);
                    return new InvalidTokenException(this.getMessage("jwttoken.exception.token_not_found"));
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

        this.jwtTokenRepository.save(jwtToken.incrementUsage(this.clockService.now()));

        return this.validationResponseMapper.toResponse(jwtToken, claims, expiry);
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

    private String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.messageSource.getMessage(code, args, code, locale);
    }
}
