package com.spacecodee.securityspacee.jwttoken.application.usecase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeAllSessionTokensCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeAllSessionTokensUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.application.response.RevocationSummary;
import com.spacecodee.securityspacee.jwttoken.domain.event.AllSessionTokensRevokedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.event.TokenRevokedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenAlreadyRevokedException;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.repository.IJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;

public class RevokeAllSessionTokensUseCase implements IRevokeAllSessionTokensUseCase {

    private static final Logger log = LoggerFactory.getLogger(RevokeAllSessionTokensUseCase.class);

    private final IJwtTokenRepository jwtTokenRepository;
    private final IClockService clockService;
    private final ApplicationEventPublisher eventPublisher;

    public RevokeAllSessionTokensUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.clockService = clockService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public @NonNull RevocationSummary execute(@NonNull RevokeAllSessionTokensCommand command) {
        List<JwtToken> tokens = this.jwtTokenRepository.findBySessionId(command.sessionId());

        if (tokens.isEmpty()) {
            return RevocationSummary.builder()
                    .tokensRevokedCount(0)
                    .revokedJtis(List.of())
                    .build();
        }

        List<JwtToken> activeTokens = tokens.stream()
                .filter(token -> token.getState() == TokenState.ACTIVE)
                .toList();

        Instant now = this.clockService.now();
        List<String> revokedJtis = new ArrayList<>();

        for (JwtToken token : activeTokens) {
            try {
                JwtToken revokedToken = token.revoke(command.revokedBy(), command.reason(), now);
                this.jwtTokenRepository.save(revokedToken);
                revokedJtis.add(token.getJti().toString());

                this.publishTokenRevokedEvent(revokedToken);
            } catch (TokenAlreadyRevokedException _) {
                log.warn("Token {} already revoked", token.getJti());
            }
        }

        if (!revokedJtis.isEmpty()) {
            this.publishAllSessionTokensRevokedEvent(command, revokedJtis.size(), now,
                    activeTokens.getFirst().getUserId());
        }

        return RevocationSummary.builder()
                .tokensRevokedCount(revokedJtis.size())
                .revokedJtis(revokedJtis)
                .build();
    }

    private void publishTokenRevokedEvent(@NonNull JwtToken token) {
        TokenRevokedEvent event = TokenRevokedEvent.builder()
                .jti(token.getJti().toString())
                .tokenType(token.getTokenType())
                .userId(token.getUserId())
                .sessionId(token.getSessionId())
                .revokedAt(token.getRevocationInfo().getRevokedAt())
                .revokedBy(token.getRevocationInfo().getRevokedBy())
                .reason(token.getRevocationInfo().getReason())
                .state(token.getState())
                .build();

        this.eventPublisher.publishEvent(event);
    }

    private void publishAllSessionTokensRevokedEvent(
            @NonNull RevokeAllSessionTokensCommand command,
            int tokensRevokedCount,
            @NonNull Instant revokedAt,
            Integer userId) {
        AllSessionTokensRevokedEvent event = AllSessionTokensRevokedEvent.builder()
                .sessionId(command.sessionId())
                .userId(userId)
                .tokensRevokedCount(tokensRevokedCount)
                .revokedAt(revokedAt)
                .revokedBy(command.revokedBy())
                .reason(command.reason())
                .build();

        this.eventPublisher.publishEvent(event);
    }
}
