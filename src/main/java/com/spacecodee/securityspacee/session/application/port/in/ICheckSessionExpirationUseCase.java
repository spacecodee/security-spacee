package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.CheckSessionExpirationQuery;
import com.spacecodee.securityspacee.session.application.response.SessionExpirationStatus;

public interface ICheckSessionExpirationUseCase {

    @NonNull
    SessionExpirationStatus execute(@NonNull CheckSessionExpirationQuery query);
}
