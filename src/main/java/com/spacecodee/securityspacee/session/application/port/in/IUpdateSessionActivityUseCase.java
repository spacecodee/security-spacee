package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.UpdateSessionActivityCommand;

public interface IUpdateSessionActivityUseCase {

    void execute(@NonNull UpdateSessionActivityCommand command);
}
