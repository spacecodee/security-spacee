package com.spacecodee.securityspacee.jwttoken.application.usecase;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.domain.event.TokenRevokedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenNotFoundException;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.repository.IJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.shared.application.port.out.IMessageResolverPort;

public final class RevokeTokenUseCase implements IRevokeTokenUseCase {

    private final IJwtTokenRepository jwtTokenRepository;
    private final IClockService clockService;
    private final ApplicationEventPublisher eventPublisher;
    private final IMessageResolverPort messageResolverPort;

    public RevokeTokenUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher,
            IMessageResolverPort messageResolverPort) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.clockService = clockService;
        this.eventPublisher = eventPublisher;
        this.messageResolverPort = messageResolverPort;
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

    private @NonNull String getMessage(String code, Object... args) {
        return this.messageResolverPort.getMessage(code, args);
    }
}
