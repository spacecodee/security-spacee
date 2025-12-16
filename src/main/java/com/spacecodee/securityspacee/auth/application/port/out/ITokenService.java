package com.spacecodee.securityspacee.auth.application.port.out;

public interface ITokenService {

    TokenPair issueTokens(IssueTokenCommand command);
}
