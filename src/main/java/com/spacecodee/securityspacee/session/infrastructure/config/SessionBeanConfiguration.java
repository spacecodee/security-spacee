package com.spacecodee.securityspacee.session.infrastructure.config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spacecodee.securityspacee.session.application.eventlistener.CreateSessionOnLoginEventListener;
import com.spacecodee.securityspacee.session.application.eventlistener.UpdateSessionActivityEventListener;
import com.spacecodee.securityspacee.session.application.mapper.ISessionResponseMapper;
import com.spacecodee.securityspacee.session.application.mapper.impl.SessionResponseMapperImpl;
import com.spacecodee.securityspacee.session.application.port.in.ICreateSessionUseCase;
import com.spacecodee.securityspacee.session.application.port.in.IGetSessionUseCase;
import com.spacecodee.securityspacee.session.application.port.in.ILogoutSessionUseCase;
import com.spacecodee.securityspacee.session.application.port.in.IUpdateSessionActivityUseCase;
import com.spacecodee.securityspacee.session.application.usecase.SessionActivityUpdater;
import com.spacecodee.securityspacee.session.application.usecase.SessionCreator;
import com.spacecodee.securityspacee.session.application.usecase.SessionFinder;
import com.spacecodee.securityspacee.session.application.usecase.SessionLogoutHandler;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.infrastructure.config.properties.SessionProperties;
import com.spacecodee.securityspacee.session.infrastructure.persistence.SessionPersistenceAdapter;
import com.spacecodee.securityspacee.session.infrastructure.persistence.jpa.SpringJpaSessionRepository;
import com.spacecodee.securityspacee.session.infrastructure.persistence.mapper.ISessionPersistenceMapper;
import com.spacecodee.securityspacee.session.infrastructure.persistence.mapper.impl.SessionPersistenceMapperImpl;

@Configuration
@EnableConfigurationProperties(SessionProperties.class)
public class SessionBeanConfiguration {

    @Bean
    public @NonNull ISessionPersistenceMapper sessionPersistenceMapper() {
        return new SessionPersistenceMapperImpl();
    }

    @Bean
    public @NonNull ISessionRepository sessionRepository(
            @NonNull SpringJpaSessionRepository jpaRepository,
            @NonNull ISessionPersistenceMapper mapper) {
        return new SessionPersistenceAdapter(jpaRepository, mapper);
    }

    @Bean
    public @NonNull ISessionResponseMapper sessionResponseMapper() {
        return new SessionResponseMapperImpl();
    }

    @Bean
    public @NonNull ICreateSessionUseCase createSessionUseCase(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ISessionResponseMapper responseMapper,
            @NonNull ApplicationEventPublisher eventPublisher,
            @NonNull SessionProperties sessionProperties) {
        return new SessionCreator(sessionRepository, responseMapper, eventPublisher, sessionProperties);
    }

    @Bean
    public @NonNull IUpdateSessionActivityUseCase updateSessionActivityUseCase(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ISessionResponseMapper responseMapper,
            @NonNull ApplicationEventPublisher eventPublisher) {
        return new SessionActivityUpdater(sessionRepository, responseMapper, eventPublisher);
    }

    @Bean
    public @NonNull ILogoutSessionUseCase logoutSessionUseCase(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ApplicationEventPublisher eventPublisher) {
        return new SessionLogoutHandler(sessionRepository, eventPublisher);
    }

    @Bean
    public @NonNull IGetSessionUseCase getSessionUseCase(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ISessionResponseMapper responseMapper) {
        return new SessionFinder(sessionRepository, responseMapper);
    }

    @Bean
    public @NonNull CreateSessionOnLoginEventListener createSessionOnLoginEventListener(
            @NonNull ICreateSessionUseCase createSessionUseCase) {
        return new CreateSessionOnLoginEventListener(createSessionUseCase);
    }

    @Bean
    public @NonNull UpdateSessionActivityEventListener updateSessionActivityEventListener(
            @NonNull ISessionRepository sessionRepository) {
        return new UpdateSessionActivityEventListener(sessionRepository);
    }
}
