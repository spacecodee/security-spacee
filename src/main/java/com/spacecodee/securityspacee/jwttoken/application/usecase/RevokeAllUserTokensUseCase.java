package com.spacecodee.securityspacee.jwttoken.application.usecase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeAllUserTokensCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeAllUserTokensUseCase;
import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;
import com.spacecodee.securityspacee.jwttoken.application.response.UserRevocationSummary;
import com.spacecodee.securityspacee.jwttoken.domain.event.AllUserTokensRevokedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.event.TokenRevokedEvent;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenAlreadyRevokedException;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.repository.IJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;

public class RevokeAllUserTokensUseCase implements IRevokeAllUserTokensUseCase {

    private static final Logger log = LoggerFactory.getLogger(RevokeAllUserTokensUseCase.class);

    private final IJwtTokenRepository jwtTokenRepository;
    private final IClockService clockService;
    private final ApplicationEventPublisher eventPublisher;

    public RevokeAllUserTokensUseCase(
            IJwtTokenRepository jwtTokenRepository,
            IClockService clockService,
            ApplicationEventPublisher eventPublisher) {
        this.jwtTokenRepository = jwtTokenRepository;
        this.clockService = clockService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public @NonNull UserRevocationSummary execute(@NonNull RevokeAllUserTokensCommand command) {
        List<JwtToken> tokens = this.jwtTokenRepository.findByUserId(command.userId());

        if (tokens.isEmpty()) {
            return UserRevocationSummary.builder()
                    .tokensRevokedCount(0)
                    .sessionsAffectedCount(0)
                    .affectedSessions(List.of())
                    .build();
        }

        List<JwtToken> activeTokens = tokens.stream()
                .filter(token -> token.getState() == TokenState.ACTIVE)
                .toList();

        Instant now = this.clockService.now();
        List<String> revokedJtis = new ArrayList<>();
        Set<String> affectedSessions = new HashSet<>();

        for (JwtToken token : activeTokens) {
            try {
                JwtToken revokedToken = token.revoke(command.revokedBy(), command.reason(), now);
                this.jwtTokenRepository.save(revokedToken);
                revokedJtis.add(token.getJti().toString());

                if (token.getSessionId() != null) {
                    affectedSessions.add(token.getSessionId());
                }

                this.publishTokenRevokedEvent(revokedToken);
            } catch (TokenAlreadyRevokedException _) {
                log.warn("Token {} already revoked", token.getJti());
            }
        }

        if (!revokedJtis.isEmpty()) {
            this.publishAllUserTokensRevokedEvent(command, revokedJtis.size(), affectedSessions.size(), now);
        }

        return UserRevocationSummary.builder()
                .tokensRevokedCount(revokedJtis.size())
                .sessionsAffectedCount(affectedSessions.size())
                .affectedSessions(new ArrayList<>(affectedSessions))
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

    private void publishAllUserTokensRevokedEvent(
            @NonNull RevokeAllUserTokensCommand command,
            int tokensRevokedCount,
            int sessionsAffectedCount,
            @NonNull Instant revokedAt) {
        AllUserTokensRevokedEvent event = AllUserTokensRevokedEvent.builder()
                .userId(command.userId())
                .tokensRevokedCount(tokensRevokedCount)
                .sessionsAffectedCount(sessionsAffectedCount)
                .revokedAt(revokedAt)
                .revokedBy(command.revokedBy())
                .reason(command.reason())
                .build();

        this.eventPublisher.publishEvent(event);
    }
}
