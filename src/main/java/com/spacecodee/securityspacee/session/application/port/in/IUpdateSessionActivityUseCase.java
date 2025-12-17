package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.UpdateSessionActivityCommand;
import com.spacecodee.securityspacee.session.application.response.SessionResponse;

public interface IUpdateSessionActivityUseCase {

    @NonNull
    SessionResponse execute(@NonNull UpdateSessionActivityCommand command);
}
