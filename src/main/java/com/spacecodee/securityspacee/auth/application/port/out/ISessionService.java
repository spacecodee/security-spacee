package com.spacecodee.securityspacee.auth.application.port.out;

public interface ISessionService {

    void createSession(AuthCreateSessionCommand command);
}
