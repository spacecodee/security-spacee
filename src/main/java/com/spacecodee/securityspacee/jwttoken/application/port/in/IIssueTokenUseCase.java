package com.spacecodee.securityspacee.jwttoken.application.port.in;

import com.spacecodee.securityspacee.jwttoken.application.command.IssueTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenPairResponse;

public interface IIssueTokenUseCase {

    TokenPairResponse execute(IssueTokenCommand command);
}
