package com.spacecodee.securityspacee.jwttoken.application.port.in;

import com.spacecodee.securityspacee.jwttoken.application.command.ValidateTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenValidationResponse;

public interface IValidateTokenUseCase {

    TokenValidationResponse execute(ValidateTokenCommand command);
}
