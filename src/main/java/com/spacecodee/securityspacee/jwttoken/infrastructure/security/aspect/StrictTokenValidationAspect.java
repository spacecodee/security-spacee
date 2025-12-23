package com.spacecodee.securityspacee.jwttoken.infrastructure.security.aspect;

import java.time.Instant;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.spacecodee.securityspacee.jwttoken.application.command.ValidateTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IValidateTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenValidationResponse;
import com.spacecodee.securityspacee.jwttoken.domain.event.TokenValidatedInStrictModeEvent;
import com.spacecodee.securityspacee.jwttoken.domain.exception.InvalidTokenException;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenMetadata;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.ValidationMode;
import com.spacecodee.securityspacee.jwttoken.infrastructure.security.annotation.ValidateTokenStrict;
import com.spacecodee.securityspacee.shared.application.port.out.IMessageResolverPort;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public final class StrictTokenValidationAspect {

    private final IValidateTokenUseCase validateTokenUseCase;
    private final ApplicationEventPublisher eventPublisher;
    private final IMessageResolverPort messageResolverPort;
    private final IClockService clockService;
    private final HttpServletRequest request;

    @Before("@annotation(validateTokenStrict)")
    public void validateTokenInStrictMode(@NonNull ValidateTokenStrict validateTokenStrict) {
        String token = this.extractTokenFromRequest();

        if (token == null) {
            throw new InvalidTokenException(
                    this.messageResolverPort.getMessage("jwttoken.exception.missing_token"));
        }

        ValidateTokenCommand command = new ValidateTokenCommand(token, ValidationMode.STRICT);

        TokenValidationResponse result = this.validateTokenUseCase.execute(command);

        if (!result.valid()) {
            log.warn("Strict validation failed for endpoint {}: token invalid",
                    this.request.getRequestURI());

            throw new InvalidTokenException(
                    this.messageResolverPort.getMessage("jwttoken.exception.token_invalid"));
        }

        log.info("Strict validation passed for user {} on critical operation: {}",
                result.userId(),
                validateTokenStrict.reason());

        this.publishValidationEvent(result);
    }

    private void publishValidationEvent(@NonNull TokenValidationResponse result) {
        Instant now = this.clockService.now();

        TokenMetadata metadata = result.metadata();
        Integer usageCount = null;
        if (metadata != null) {
            usageCount = metadata.usageCount();
        }

        TokenValidatedInStrictModeEvent event = TokenValidatedInStrictModeEvent.builder()
                .jti(result.jti())
                .userId(result.userId())
                .sessionId(result.sessionId())
                .validatedAt(now)
                .usageCount(usageCount)
                .ipAddress(this.request.getRemoteAddr())
                .endpoint(this.request.getRequestURI())
                .build();

        this.eventPublisher.publishEvent(event);
    }

    private @Nullable String extractTokenFromRequest() {
        String bearerToken = this.request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
