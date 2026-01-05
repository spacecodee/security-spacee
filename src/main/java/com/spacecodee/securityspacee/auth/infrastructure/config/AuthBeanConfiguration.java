package com.spacecodee.securityspacee.auth.infrastructure.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spacecodee.securityspacee.auth.adapter.mapper.ILoginRequestMapper;
import com.spacecodee.securityspacee.auth.adapter.mapper.IRefreshTokenRequestMapper;
import com.spacecodee.securityspacee.auth.adapter.mapper.impl.LoginRequestMapperImpl;
import com.spacecodee.securityspacee.auth.adapter.mapper.impl.RefreshTokenRequestMapperImpl;
import com.spacecodee.securityspacee.auth.application.mapper.IAuthenticationResponseMapper;
import com.spacecodee.securityspacee.auth.application.mapper.impl.AuthenticationResponseMapperImpl;
import com.spacecodee.securityspacee.auth.application.port.in.ILoginUseCase;
import com.spacecodee.securityspacee.auth.application.port.out.IPasswordValidator;
import com.spacecodee.securityspacee.auth.application.port.out.ISessionService;
import com.spacecodee.securityspacee.auth.application.port.out.ITokenService;
import com.spacecodee.securityspacee.auth.application.port.out.IUserAuthenticationPort;
import com.spacecodee.securityspacee.auth.application.usecase.LoginUseCase;
import com.spacecodee.securityspacee.auth.infrastructure.adapter.PasswordValidatorAdapter;
import com.spacecodee.securityspacee.auth.infrastructure.adapter.SessionServiceAdapter;
import com.spacecodee.securityspacee.auth.infrastructure.adapter.TokenServiceAdapter;
import com.spacecodee.securityspacee.auth.infrastructure.adapter.UserAuthenticationAdapter;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IIssueTokenUseCase;
import com.spacecodee.securityspacee.session.application.port.in.ICreateSessionUseCase;
import com.spacecodee.securityspacee.shared.application.port.out.IClientIpExtractorPort;
import com.spacecodee.securityspacee.shared.application.port.out.IMessageResolverPort;
import com.spacecodee.securityspacee.shared.config.properties.SecurityProperties;
import com.spacecodee.securityspacee.user.application.port.out.IPasswordEncoder;
import com.spacecodee.securityspacee.user.domain.repository.IUserRepository;

@Configuration
public class AuthBeanConfiguration {

    @Bean
    public IUserAuthenticationPort userAuthenticationPort(
            IUserRepository userRepository,
            SecurityProperties securityProperties) {
        return new UserAuthenticationAdapter(userRepository, securityProperties);
    }

    @Bean
    public IPasswordValidator passwordValidator(IPasswordEncoder passwordEncoder) {
        return new PasswordValidatorAdapter(passwordEncoder);
    }

    @Bean
    public ITokenService tokenService(IIssueTokenUseCase issueTokenUseCase) {
        return new TokenServiceAdapter(issueTokenUseCase);
    }

    @Bean
    public ISessionService sessionService(ICreateSessionUseCase createSessionUseCase) {
        return new SessionServiceAdapter(createSessionUseCase);
    }

    @Bean
    public ILoginUseCase loginUseCase(
            IUserAuthenticationPort userAuthenticationPort,
            IPasswordValidator passwordValidator,
            ITokenService tokenService,
            ISessionService sessionService,
            ApplicationEventPublisher eventPublisher,
            IAuthenticationResponseMapper responseMapper,
            IMessageResolverPort messageResolverPort,
            SecurityProperties securityProperties) {
        return new LoginUseCase(
                userAuthenticationPort,
                passwordValidator,
                tokenService,
                sessionService,
                eventPublisher,
                responseMapper,
                messageResolverPort,
                securityProperties);
    }

    @Bean
    public IAuthenticationResponseMapper authenticationResponseMapper() {
        return new AuthenticationResponseMapperImpl();
    }

    @Bean
    public ILoginRequestMapper loginRequestMapper(IClientIpExtractorPort clientIpExtractorPort) {
        return new LoginRequestMapperImpl(clientIpExtractorPort);
    }

    @Bean
    public IRefreshTokenRequestMapper refreshTokenRequestMapper(IClientIpExtractorPort clientIpExtractorPort) {
        return new RefreshTokenRequestMapperImpl(clientIpExtractorPort);
    }
}
