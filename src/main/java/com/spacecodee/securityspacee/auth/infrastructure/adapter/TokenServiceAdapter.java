package com.spacecodee.securityspacee.auth.infrastructure.adapter;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.auth.application.port.out.AuthIssueTokenCommand;
import com.spacecodee.securityspacee.auth.application.port.out.ITokenService;
import com.spacecodee.securityspacee.auth.application.port.out.TokenPair;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IIssueTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenPairResponse;

public final class TokenServiceAdapter implements ITokenService {

    private final IIssueTokenUseCase issueTokenUseCase;

    public TokenServiceAdapter(@NonNull IIssueTokenUseCase issueTokenUseCase) {
        this.issueTokenUseCase = issueTokenUseCase;
    }

    @Override
    public @NonNull TokenPair issueTokens(@NonNull AuthIssueTokenCommand command) {
        com.spacecodee.securityspacee.jwttoken.application.command.IssueTokenCommand jwtCommand = new com.spacecodee.securityspacee.jwttoken.application.command.IssueTokenCommand(
                command.userId(),
                command.username(),
                "session-placeholder",
                command.roles(),
                "ip-placeholder",
                "user-agent-placeholder");

        TokenPairResponse response = this.issueTokenUseCase.execute(jwtCommand);

        return new TokenPair(
                response.accessToken(),
                response.refreshToken(),
                (int) response.expiresIn());
    }
}
