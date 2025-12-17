package com.spacecodee.securityspacee.jwttoken.application.usecase;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.spacecodee.securityspacee.jwttoken.application.command.RefreshTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.mapper.IClaimsMapper;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRefreshTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IJwtCryptoService;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenPairResponse;
import com.spacecodee.securityspacee.jwttoken.domain.event.TokenRefreshedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.exception.InvalidTokenException;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.repository.IJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Claims;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenType;
import com.spacecodee.securityspacee.shared.config.properties.JwtProperties;

public final class RefreshTokenUseCase implements IRefreshTokenUseCase {

    private final IJwtTokenRepository jwtTokenRepository;
    private final IJwtCryptoService jwtCryptoService;
    private final IClockService clockService;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtProperties jwtProperties;
    private final MessageSource messageSource;
    private final IClaimsMapper claimsMapper;

    public RefreshTokenUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IJwtCryptoService jwtCryptoService,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher,
            JwtProperties jwtProperties,
            MessageSource messageSource,
            IClaimsMapper claimsMapper) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.jwtCryptoService = jwtCryptoService;
        this.clockService = clockService;
        this.eventPublisher = eventPublisher;
        this.jwtProperties = jwtProperties;
        this.messageSource = messageSource;
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

        if (!refreshToken.isActive()) {
            throw new InvalidTokenException(this.getMessage("jwttoken.exception.token_not_active"));
        }

        Jti newAccessJti = Jti.generate();
        Instant now = this.clockService.now();
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
                .build();

        this.jwtTokenRepository.save(newAccessToken);

        JwtToken updatedRefreshToken = refreshToken.incrementRefresh(now);
        this.jwtTokenRepository.save(updatedRefreshToken);

        TokenRefreshedEvent event = TokenRefreshedEvent.builder()
                .oldAccessTokenJti(null)
                .newAccessTokenJti(newAccessJti.getValue().toString())
                .refreshTokenJti(refreshJti.getValue().toString())
                .userId(updatedRefreshToken.getUserId())
                .refreshedAt(now)
                .refreshCount(updatedRefreshToken.getRefreshCount())
                .build();

        this.eventPublisher.publishEvent(event);

        return new TokenPairResponse(
                newAccessTokenRaw,
                command.refreshToken(),
                this.jwtProperties.accessExpiration());
    }

    private String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.messageSource.getMessage(code, args, code, locale);
    }
}
