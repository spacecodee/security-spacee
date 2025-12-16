package com.spacecodee.securityspacee.auth.application.port.out;

public interface ISessionService {

    void createSession(CreateSessionCommand command);
}
