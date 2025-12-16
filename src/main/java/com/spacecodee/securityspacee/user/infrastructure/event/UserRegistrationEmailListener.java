package com.spacecodee.securityspacee.user.infrastructure.event;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.spacecodee.securityspacee.user.application.port.out.IEmailServicePort;
import com.spacecodee.securityspacee.user.domain.event.UserRegisteredEvent;

@Component
public class UserRegistrationEmailListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationEmailListener.class);

    private final IEmailServicePort emailService;

    public UserRegistrationEmailListener(IEmailServicePort emailService) {
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void handleUserRegistered(@NonNull UserRegisteredEvent event) {
        log.info("Handling UserRegisteredEvent for user: {}", event.getUsername());

        try {
            emailService.sendWelcomeEmail(event.getEmail(), event.getUsername());
            log.info("Welcome email sent successfully to: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", event.getEmail(), e);
        }
    }
}
