package com.spacecodee.securityspacee.jwttoken.infrastructure.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spacecodee.securityspacee.jwttoken.application.mapper.IClaimsMapper;
import com.spacecodee.securityspacee.jwttoken.application.mapper.ITokenPairMapper;
import com.spacecodee.securityspacee.jwttoken.application.mapper.ITokenValidationResponseMapper;
import com.spacecodee.securityspacee.jwttoken.application.mapper.impl.ClaimsMapperImpl;
import com.spacecodee.securityspacee.jwttoken.application.mapper.impl.TokenPairMapperImpl;
import com.spacecodee.securityspacee.jwttoken.application.mapper.impl.TokenValidationResponseMapperImpl;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IIssueTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRefreshTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IValidateTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IJwtCryptoService;
import com.spacecodee.securityspacee.jwttoken.application.usecase.IssueTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.usecase.RefreshTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.usecase.RevokeTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.usecase.ValidateTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.domain.repository.IJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.infrastructure.clock.ClockServiceAdapter;
import com.spacecodee.securityspacee.jwttoken.infrastructure.crypto.JwtCryptoServiceAdapter;
import com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.JwtTokenPersistenceAdapter;
import com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.jpa.SpringJpaJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.mapper.IJwtTokenPersistenceMapper;
import com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.mapper.impl.JwtTokenPersistenceMapperImpl;
import com.spacecodee.securityspacee.shared.application.port.out.IMessageResolverPort;
import com.spacecodee.securityspacee.shared.config.properties.JwtProperties;

@Configuration
public class JwtTokenBeanConfiguration {

    @Bean
    public IClaimsMapper claimsMapper() {
        return new ClaimsMapperImpl();
    }

    @Bean
    public ITokenPairMapper tokenPairMapper() {
        return new TokenPairMapperImpl();
    }

    @Bean
    public ITokenValidationResponseMapper tokenValidationResponseMapper() {
        return new TokenValidationResponseMapperImpl();
    }

    @Bean
    public IJwtTokenPersistenceMapper jwtTokenPersistenceMapper() {
        return new JwtTokenPersistenceMapperImpl();
    }

    @Bean
    public IJwtTokenRepository jwtTokenRepository(
            SpringJpaJwtTokenRepository springJpaRepository,
            IJwtTokenPersistenceMapper mapper) {
        return new JwtTokenPersistenceAdapter(springJpaRepository, mapper);
    }

    @Bean
    public IJwtCryptoService jwtCryptoService(JwtProperties jwtProperties) {
        return new JwtCryptoServiceAdapter(jwtProperties);
    }

    @Bean
    public IClockService clockService() {
        return new ClockServiceAdapter();
    }

    @Bean
    public IIssueTokenUseCase issueTokenUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IJwtCryptoService jwtCryptoService,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher,
            JwtProperties jwtProperties,
            IClaimsMapper claimsMapper,
            ITokenPairMapper tokenPairMapper) {
        return new IssueTokenUseCase(
                jwtTokenRepository,
                jwtCryptoService,
                clockService,
                eventPublisher,
                jwtProperties,
                claimsMapper,
                tokenPairMapper);
    }

    @Bean
    public IValidateTokenUseCase validateTokenUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IJwtCryptoService jwtCryptoService,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher,
            IMessageResolverPort messageResolverPort,
            ITokenValidationResponseMapper validationResponseMapper) {
        return new ValidateTokenUseCase(
                jwtTokenRepository,
                jwtCryptoService,
                clockService,
                eventPublisher,
                messageResolverPort,
                validationResponseMapper);
    }

    @Bean
    public IRefreshTokenUseCase refreshTokenUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IJwtCryptoService jwtCryptoService,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher,
            JwtProperties jwtProperties,
            IMessageResolverPort messageResolverPort,
            IClaimsMapper claimsMapper) {
        return new RefreshTokenUseCase(
                jwtTokenRepository,
                jwtCryptoService,
                clockService,
                eventPublisher,
                jwtProperties,
                messageResolverPort,
                claimsMapper);
    }

    @Bean
    public IRevokeTokenUseCase revokeTokenUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher,
            IMessageResolverPort messageResolverPort) {
        return new RevokeTokenUseCase(
                jwtTokenRepository,
                clockService,
                eventPublisher,
                messageResolverPort);
    }
}
