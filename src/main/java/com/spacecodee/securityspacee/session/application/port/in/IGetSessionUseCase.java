package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.response.SessionResponse;

public interface IGetSessionUseCase {

    @NonNull
    SessionResponse execute(@NonNull String sessionToken);
}
