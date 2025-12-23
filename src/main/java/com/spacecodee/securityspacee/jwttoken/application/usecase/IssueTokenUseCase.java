package com.spacecodee.securityspacee.jwttoken.application.usecase;

import java.time.Duration;
import java.time.Instant;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;

import com.spacecodee.securityspacee.jwttoken.application.command.IssueTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.mapper.IClaimsMapper;
import com.spacecodee.securityspacee.jwttoken.application.mapper.ITokenPairMapper;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IIssueTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IJwtCryptoService;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenPairResponse;
import com.spacecodee.securityspacee.jwttoken.domain.event.TokensIssuedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.repository.IJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Claims;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenPair;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenType;
import com.spacecodee.securityspacee.shared.config.properties.JwtProperties;

public final class IssueTokenUseCase implements IIssueTokenUseCase {

    private final IJwtTokenRepository jwtTokenRepository;
    private final IJwtCryptoService jwtCryptoService;
    private final IClockService clockService;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtProperties jwtProperties;
    private final IClaimsMapper claimsMapper;
    private final ITokenPairMapper tokenPairMapper;

    public IssueTokenUseCase(
            @NonNull IJwtTokenRepository jwtTokenRepository,
            @NonNull IJwtCryptoService jwtCryptoService,
            @NonNull IClockService clockService,
            @NonNull ApplicationEventPublisher eventPublisher,
            @NonNull JwtProperties jwtProperties,
            @NonNull IClaimsMapper claimsMapper,
            @NonNull ITokenPairMapper tokenPairMapper) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.jwtCryptoService = jwtCryptoService;
        this.clockService = clockService;
        this.eventPublisher = eventPublisher;
        this.jwtProperties = jwtProperties;
        this.claimsMapper = claimsMapper;
        this.tokenPairMapper = tokenPairMapper;
    }

    @Contract("_ -> new")
    @Override
    public @NonNull TokenPairResponse execute(@NonNull IssueTokenCommand command) {
        // Generate unique identifiers for both tokens
        Jti accessJti = Jti.generate();
        Jti refreshJti = Jti.generate();

        // Calculate time references
        Instant now = this.clockService.now();
        Instant accessExpiry = this.clockService
                .nowPlusDuration(Duration.ofSeconds(this.jwtProperties.accessExpiration()));
        Instant refreshExpiry = this.clockService
                .nowPlusDuration(Duration.ofSeconds(this.jwtProperties.refreshExpiration()));

        // Map command to Claims using mapper
        Claims accessClaims = this.claimsMapper.buildAccessClaims(
                accessJti, command, now, accessExpiry, this.jwtProperties);
        Claims refreshClaims = this.claimsMapper.buildRefreshClaims(
                refreshJti, command, now, refreshExpiry, this.jwtProperties);

        // Generate raw JWT strings
        String accessTokenRaw = this.jwtCryptoService.generateToken(
                accessClaims.toMap(),
                Duration.ofSeconds(this.jwtProperties.accessExpiration()));
        String refreshTokenRaw = this.jwtCryptoService.generateToken(
                refreshClaims.toMap(),
                Duration.ofSeconds(this.jwtProperties.refreshExpiration()));

        // Create domain JwtToken aggregates
        JwtToken accessToken = JwtToken.builder()
                .jti(accessJti)
                .tokenType(TokenType.ACCESS)
                .rawToken(accessTokenRaw)
                .userId(command.userId())
                .sessionId(command.sessionId())
                .state(TokenState.ACTIVE)
                .issuedAt(now)
                .expiryDate(accessExpiry)
                .claims(accessClaims)
                .clientIp(command.ipAddress())
                .userAgent(command.userAgent())
                .usageCount(0)
                .refreshCount(0)
                .build();

        JwtToken refreshToken = JwtToken.builder()
                .jti(refreshJti)
                .tokenType(TokenType.REFRESH)
                .rawToken(refreshTokenRaw)
                .userId(command.userId())
                .sessionId(command.sessionId())
                .state(TokenState.ACTIVE)
                .issuedAt(now)
                .expiryDate(refreshExpiry)
                .claims(refreshClaims)
                .clientIp(command.ipAddress())
                .userAgent(command.userAgent())
                .usageCount(0)
                .refreshCount(0)
                .build();

        // Persist both tokens
        this.jwtTokenRepository.save(accessToken);
        this.jwtTokenRepository.save(refreshToken);

        // Create and publish domain event
        TokensIssuedEvent event = TokensIssuedEvent.builder()
                .accessTokenJti(accessJti.getValue().toString())
                .refreshTokenJti(refreshJti.getValue().toString())
                .userId(command.userId())
                .sessionId(command.sessionId())
                .issuedAt(now)
                .accessTokenExpiresAt(accessExpiry)
                .refreshTokenExpiresAt(refreshExpiry)
                .build();
        this.eventPublisher.publishEvent(event);

        // Create response using mapper
        TokenPair tokenPair = TokenPair.builder()
                .accessToken(accessTokenRaw)
                .refreshToken(refreshTokenRaw)
                .expiresIn(this.jwtProperties.accessExpiration())
                .build();

        return this.tokenPairMapper.toResponse(tokenPair);
    }
}
