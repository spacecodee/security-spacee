package com.spacecodee.securityspacee.auth.infrastructure.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spacecodee.securityspacee.auth.adapter.mapper.ILoginRequestMapper;
import com.spacecodee.securityspacee.auth.adapter.mapper.impl.LoginRequestMapperImpl;
import com.spacecodee.securityspacee.auth.application.mapper.IAuthenticationResponseMapper;
import com.spacecodee.securityspacee.auth.application.mapper.impl.AuthenticationResponseMapperImpl;
import com.spacecodee.securityspacee.auth.application.port.in.ILoginUseCase;
import com.spacecodee.securityspacee.auth.application.port.out.IPasswordValidator;
import com.spacecodee.securityspacee.auth.application.port.out.ISessionService;
import com.spacecodee.securityspacee.auth.application.port.out.ITokenService;
import com.spacecodee.securityspacee.auth.application.port.out.IUserAuthenticationPort;
import com.spacecodee.securityspacee.auth.application.usecase.LoginUseCase;
import com.spacecodee.securityspacee.shared.config.properties.SecurityProperties;

@Configuration
public class AuthBeanConfiguration {

    @Bean
    public ILoginUseCase loginUseCase(
            IUserAuthenticationPort userAuthenticationPort,
            IPasswordValidator passwordValidator,
            ITokenService tokenService,
            ISessionService sessionService,
            ApplicationEventPublisher eventPublisher,
            IAuthenticationResponseMapper responseMapper,
            MessageSource messageSource,
            SecurityProperties securityProperties) {
        return new LoginUseCase(
                userAuthenticationPort,
                passwordValidator,
                tokenService,
                sessionService,
                eventPublisher,
                responseMapper,
                messageSource,
                securityProperties);
    }

    @Bean
    public IAuthenticationResponseMapper authenticationResponseMapper() {
        return new AuthenticationResponseMapperImpl();
    }

    @Bean
    public ILoginRequestMapper loginRequestMapper() {
        return new LoginRequestMapperImpl();
    }
}
