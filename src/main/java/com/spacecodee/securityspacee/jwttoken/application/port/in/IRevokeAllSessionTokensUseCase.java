package com.spacecodee.securityspacee.jwttoken.application.port.in;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeAllSessionTokensCommand;
import com.spacecodee.securityspacee.jwttoken.application.response.RevocationSummary;

public interface IRevokeAllSessionTokensUseCase {

    RevocationSummary execute(RevokeAllSessionTokensCommand command);
}
