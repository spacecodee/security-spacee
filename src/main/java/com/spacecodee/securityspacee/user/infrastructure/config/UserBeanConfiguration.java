package com.spacecodee.securityspacee.user.infrastructure.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import com.spacecodee.securityspacee.user.adapter.mapper.IUserRestMapper;
import com.spacecodee.securityspacee.user.adapter.mapper.impl.UserRestMapperImpl;
import com.spacecodee.securityspacee.user.application.mapper.IUserResponseMapper;
import com.spacecodee.securityspacee.user.application.mapper.impl.UserResponseMapperImpl;
import com.spacecodee.securityspacee.user.application.port.in.IRegisterUserUseCase;
import com.spacecodee.securityspacee.user.application.port.out.IEmailServicePort;
import com.spacecodee.securityspacee.user.application.port.out.IPasswordEncoder;
import com.spacecodee.securityspacee.user.application.usecase.RegisterUserUseCase;
import com.spacecodee.securityspacee.user.domain.repository.IUserRepository;
import com.spacecodee.securityspacee.user.infrastructure.email.SmtpEmailAdapter;
import com.spacecodee.securityspacee.user.infrastructure.persistence.UserPersistenceAdapter;
import com.spacecodee.securityspacee.user.infrastructure.persistence.jpa.SpringJpaUserRepository;
import com.spacecodee.securityspacee.user.infrastructure.persistence.mapper.IUserPersistenceMapper;
import com.spacecodee.securityspacee.user.infrastructure.persistence.mapper.impl.UserPersistenceMapperImpl;
import com.spacecodee.securityspacee.user.infrastructure.security.BCryptPasswordEncoderAdapter;

@Configuration
public class UserBeanConfiguration {

    @Bean
    public IUserPersistenceMapper userPersistenceMapper() {
        return new UserPersistenceMapperImpl();
    }

    @Bean
    public IUserRepository userRepository(
            SpringJpaUserRepository springJpaUserRepository,
            IUserPersistenceMapper persistenceMapper) {
        return new UserPersistenceAdapter(springJpaUserRepository, persistenceMapper);
    }

    @Bean
    public IPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoderAdapter();
    }

    @Bean
    public IUserResponseMapper userResponseMapper() {
        return new UserResponseMapperImpl();
    }

    @Bean
    public IRegisterUserUseCase registerUserUseCase(
            IUserRepository userRepository,
            IPasswordEncoder passwordEncoder,
            IUserResponseMapper responseMapper,
            ApplicationEventPublisher eventPublisher) {
        return new RegisterUserUseCase(userRepository, passwordEncoder, responseMapper, eventPublisher);
    }

    @Bean
    public IUserRestMapper userRestMapper() {
        return new UserRestMapperImpl();
    }

    @Bean
    public IEmailServicePort emailService(JavaMailSender mailSender, MessageSource messageSource) {
        return new SmtpEmailAdapter(mailSender, messageSource);
    }
}
