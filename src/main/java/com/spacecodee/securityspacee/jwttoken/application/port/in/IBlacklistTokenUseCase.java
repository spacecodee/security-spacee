package com.spacecodee.securityspacee.jwttoken.application.port.in;

import com.spacecodee.securityspacee.jwttoken.application.command.BlacklistTokenCommand;

public interface IBlacklistTokenUseCase {

    void execute(BlacklistTokenCommand command);
}
