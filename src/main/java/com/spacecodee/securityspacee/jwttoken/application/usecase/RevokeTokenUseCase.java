package com.spacecodee.securityspacee.jwttoken.application.usecase;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.domain.event.TokenRevokedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenNotFoundException;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.repository.IJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;

public final class RevokeTokenUseCase implements IRevokeTokenUseCase {

    private final IJwtTokenRepository jwtTokenRepository;
    private final IClockService clockService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;

    public RevokeTokenUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher,
            MessageSource messageSource) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.clockService = clockService;
        this.eventPublisher = eventPublisher;
        this.messageSource = messageSource;
    }

    @Override
    public void execute(@NonNull RevokeTokenCommand command) {
        Jti jti = Jti.parse(command.jti());

        JwtToken token = this.jwtTokenRepository.findByJti(jti)
                .orElseThrow(() -> new TokenNotFoundException(this.getMessage("jwttoken.exception.token_not_found")));

        JwtToken revokedToken = token.revoke(command.revokedBy(), command.reason(), this.clockService.now());

        this.jwtTokenRepository.save(revokedToken);

        this.publishTokenRevokedEvent(revokedToken);
    }

    private void publishTokenRevokedEvent(@NonNull JwtToken token) {
        TokenRevokedEvent event = TokenRevokedEvent.builder()
                .jti(token.getJti().toString())
                .tokenType(token.getTokenType())
                .userId(token.getUserId())
                .revokedAt(token.getRevocationInfo().getRevokedAt())
                .revokedBy(token.getRevocationInfo().getRevokedBy())
                .reason(token.getRevocationInfo().getReason())
                .build();

        this.eventPublisher.publishEvent(event);
    }

    private String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.messageSource.getMessage(code, args, code, locale);
    }
}
