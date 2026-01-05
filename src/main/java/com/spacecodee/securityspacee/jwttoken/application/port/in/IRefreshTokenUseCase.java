package com.spacecodee.securityspacee.jwttoken.application.port.in;

import com.spacecodee.securityspacee.jwttoken.application.command.RefreshTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenPairResponse;

public interface IRefreshTokenUseCase {

    TokenPairResponse execute(RefreshTokenCommand command);
}
