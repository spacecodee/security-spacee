package com.spacecodee.securityspacee.jwttoken.application.usecase;

import java.time.Duration;
import java.time.Instant;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;

import com.spacecodee.securityspacee.jwttoken.application.command.RefreshTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.mapper.IClaimsMapper;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRefreshTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IJwtCryptoService;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenPairResponse;
import com.spacecodee.securityspacee.jwttoken.domain.event.RefreshTokenReuseDetectedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.event.TokenRefreshedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.exception.InvalidTokenException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.RevokedTokenException;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.repository.IJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Claims;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenType;
import com.spacecodee.securityspacee.shared.application.port.out.IMessageResolverPort;
import com.spacecodee.securityspacee.shared.config.properties.JwtProperties;

public final class RefreshTokenUseCase implements IRefreshTokenUseCase {

    private final IJwtTokenRepository jwtTokenRepository;
    private final IJwtCryptoService jwtCryptoService;
    private final IClockService clockService;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtProperties jwtProperties;
    private final IMessageResolverPort messageResolverPort;
    private final IClaimsMapper claimsMapper;

    public RefreshTokenUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IJwtCryptoService jwtCryptoService,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher,
            JwtProperties jwtProperties,
            IMessageResolverPort messageResolverPort,
            IClaimsMapper claimsMapper) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.jwtCryptoService = jwtCryptoService;
        this.clockService = clockService;
        this.eventPublisher = eventPublisher;
        this.jwtProperties = jwtProperties;
        this.messageResolverPort = messageResolverPort;
        this.claimsMapper = claimsMapper;
    }

    @Contract("_ -> new")
    @Override
    public @NonNull TokenPairResponse execute(@NonNull RefreshTokenCommand command) {
        String refreshJtiString = this.jwtCryptoService.getJtiFromToken(command.refreshToken());
        Jti refreshJti = Jti.parse(refreshJtiString);

        JwtToken refreshToken = this.jwtTokenRepository.findByJti(refreshJti)
                .orElseThrow(() -> new InvalidTokenException(this.getMessage("jwttoken.exception.token_not_found")));

        if (refreshToken.getTokenType() != TokenType.REFRESH) {
            throw new InvalidTokenException(this.getMessage("jwttoken.exception.not_refresh_token"));
        }

        if (refreshToken.getState() == TokenState.REVOKED) {
            RefreshTokenReuseDetectedEvent refreshTokenReuseDetectedEvent = RefreshTokenReuseDetectedEvent.builder()
                    .refreshTokenJti(refreshToken.getJti().getValue().toString())
                    .userId(refreshToken.getUserId())
                    .attemptedAt(this.clockService.now())
                    .ipAddress(command.ipAddress())
                    .sessionId(refreshToken.getSessionId())
                    .reason("jwttoken.event.refresh_token_reuse_detected")
                    .build();

            this.eventPublisher.publishEvent(refreshTokenReuseDetectedEvent);

            String message = this.getMessage("jwttoken.exception.refresh_token_revoked");
            throw new RevokedTokenException(message);
        }

        if (!refreshToken.isActive()) {
            throw new InvalidTokenException(this.getMessage("jwttoken.exception.token_not_active"));
        }

        Instant now = this.clockService.now();
        JwtToken validatedRefreshToken = refreshToken.refresh(now);

        Jti newAccessJti = Jti.generate();
        Instant accessExpiry = this.clockService
                .nowPlusDuration(Duration.ofSeconds(this.jwtProperties.accessExpiration()));

        Claims accessClaims = this.claimsMapper.buildAccessClaimsFromRefresh(
                newAccessJti, refreshToken, now, accessExpiry, this.jwtProperties);

        String newAccessTokenRaw = this.jwtCryptoService.generateToken(
                accessClaims.toMap(),
                Duration.ofSeconds(this.jwtProperties.accessExpiration()));

        JwtToken newAccessToken = JwtToken.builder()
                .jti(newAccessJti)
                .tokenType(TokenType.ACCESS)
                .rawToken(newAccessTokenRaw)
                .userId(refreshToken.getUserId())
                .sessionId(refreshToken.getSessionId())
                .state(TokenState.ACTIVE)
                .issuedAt(now)
                .expiryDate(accessExpiry)
                .claims(accessClaims)
                .clientIp(command.ipAddress())
                .userAgent(command.userAgent())
                .refreshCount(0)
                .usageCount(0)
                .build();

        this.jwtTokenRepository.save(newAccessToken);

        this.jwtTokenRepository.save(validatedRefreshToken);

        this.revokeOldAccessToken(refreshToken.getSessionId());

        TokenRefreshedEvent tokenRefreshedEvent = TokenRefreshedEvent.builder()
                .oldAccessTokenJti(null)
                .newAccessTokenJti(newAccessJti.getValue().toString())
                .refreshTokenJti(refreshJti.getValue().toString())
                .userId(refreshToken.getUserId())
                .sessionId(refreshToken.getSessionId())
                .refreshedAt(now)
                .refreshCount(validatedRefreshToken.getRefreshCount())
                .newAccessTokenExpiresAt(accessExpiry)
                .build();

        this.eventPublisher.publishEvent(tokenRefreshedEvent);

        return new TokenPairResponse(
                newAccessTokenRaw,
                command.refreshToken(),
                this.jwtProperties.accessExpiration());
    }

    private void revokeOldAccessToken(@NonNull String sessionId) {
        this.jwtTokenRepository.findLatestAccessTokenBySessionId(sessionId)
                .ifPresent(oldAccessToken -> {
                    if (oldAccessToken.isActive()) {
                        JwtToken expiredToken = oldAccessToken.markAsExpired(this.clockService.now());
                        this.jwtTokenRepository.save(expiredToken);
                    }
                });
    }

    private @NonNull String getMessage(String code, Object... args) {
        return this.messageResolverPort.getMessage(code, args);
    }
}
