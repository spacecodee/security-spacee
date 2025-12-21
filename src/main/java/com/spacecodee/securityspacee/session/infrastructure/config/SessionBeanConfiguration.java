package com.spacecodee.securityspacee.session.infrastructure.config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spacecodee.securityspacee.session.application.eventlistener.CreateSessionOnLoginEventListener;
import com.spacecodee.securityspacee.session.application.eventlistener.UpdateSessionActivityEventListener;
import com.spacecodee.securityspacee.session.application.mapper.ISessionResponseMapper;
import com.spacecodee.securityspacee.session.application.mapper.ISessionSummaryMapper;
import com.spacecodee.securityspacee.session.application.mapper.impl.SessionResponseMapperImpl;
import com.spacecodee.securityspacee.session.application.mapper.impl.SessionSummaryMapperImpl;
import com.spacecodee.securityspacee.session.application.port.in.ICreateSessionUseCase;
import com.spacecodee.securityspacee.session.application.port.in.IGetActiveSessionsUseCase;
import com.spacecodee.securityspacee.session.application.port.in.IGetSessionUseCase;
import com.spacecodee.securityspacee.session.application.port.in.ILogoutRemoteSessionUseCase;
import com.spacecodee.securityspacee.session.application.port.in.ILogoutSessionUseCase;
import com.spacecodee.securityspacee.session.application.port.in.IUpdateSessionActivityUseCase;
import com.spacecodee.securityspacee.session.application.port.out.IDeviceFingerprintService;
import com.spacecodee.securityspacee.session.application.port.out.IGeoIpService;
import com.spacecodee.securityspacee.session.application.port.out.IUserAgentParser;
import com.spacecodee.securityspacee.session.application.usecase.SessionActiveRetriever;
import com.spacecodee.securityspacee.session.application.usecase.SessionActivityUpdater;
import com.spacecodee.securityspacee.session.application.usecase.SessionCreator;
import com.spacecodee.securityspacee.session.application.usecase.SessionFinder;
import com.spacecodee.securityspacee.session.application.usecase.SessionLogoutHandler;
import com.spacecodee.securityspacee.session.application.usecase.SessionRemoteLogoutHandler;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.service.ISessionPolicyService;
import com.spacecodee.securityspacee.session.infrastructure.config.properties.SessionProperties;
import com.spacecodee.securityspacee.session.infrastructure.persistence.SessionPersistenceAdapter;
import com.spacecodee.securityspacee.session.infrastructure.persistence.jpa.SpringJpaSessionRepository;
import com.spacecodee.securityspacee.session.infrastructure.persistence.mapper.ISessionPersistenceMapper;
import com.spacecodee.securityspacee.session.infrastructure.persistence.mapper.impl.SessionPersistenceMapperImpl;
import com.spacecodee.securityspacee.session.infrastructure.service.DefaultSessionPolicyService;
import com.spacecodee.securityspacee.session.infrastructure.service.DeviceFingerprintServiceAdapter;
import com.spacecodee.securityspacee.session.infrastructure.service.DisabledGeoIpService;
import com.spacecodee.securityspacee.session.infrastructure.service.UAParserAdapter;

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
    public @NonNull ISessionSummaryMapper sessionSummaryMapper() {
        return new SessionSummaryMapperImpl();
    }

    @Bean
    public @NonNull ISessionPolicyService sessionPolicyService(
            @NonNull SessionProperties sessionProperties) {
        return new DefaultSessionPolicyService(
                sessionProperties.concurrent().defaultMax(),
                sessionProperties.concurrent().defaultStrategy(),
                sessionProperties.concurrent().notifyNewDevice());
    }

    @Bean
    public @NonNull IDeviceFingerprintService deviceFingerprintService() {
        return new DeviceFingerprintServiceAdapter();
    }

    @Bean
    public @NonNull IUserAgentParser userAgentParser() {
        return new UAParserAdapter();
    }

    @Bean
    public @NonNull IGeoIpService geoIpService() {
        return new DisabledGeoIpService();
    }

    @Bean
    public @NonNull ICreateSessionUseCase createSessionUseCase(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ISessionResponseMapper responseMapper,
            @NonNull ApplicationEventPublisher eventPublisher,
            @NonNull SessionProperties sessionProperties,
            @NonNull ISessionPolicyService sessionPolicyService,
            @NonNull IDeviceFingerprintService deviceFingerprintService,
            @NonNull IUserAgentParser userAgentParser,
            @NonNull IGeoIpService geoIpService,
            @NonNull MessageSource messageSource) {
        return new SessionCreator(
                sessionRepository,
                responseMapper,
                eventPublisher,
                sessionProperties,
                sessionPolicyService,
                deviceFingerprintService,
                userAgentParser,
                geoIpService,
                messageSource);
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
    public @NonNull IGetActiveSessionsUseCase getActiveSessionsUseCase(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ISessionSummaryMapper sessionSummaryMapper,
            @NonNull ISessionPolicyService sessionPolicyService) {
        return new SessionActiveRetriever(sessionRepository, sessionSummaryMapper, sessionPolicyService);
    }

    @Bean
    public @NonNull ILogoutRemoteSessionUseCase logoutRemoteSessionUseCase(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ApplicationEventPublisher eventPublisher,
            @NonNull MessageSource messageSource) {
        return new SessionRemoteLogoutHandler(sessionRepository, eventPublisher, messageSource);
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
