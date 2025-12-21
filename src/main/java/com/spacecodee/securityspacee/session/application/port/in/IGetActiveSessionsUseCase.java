package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.GetActiveSessionsQuery;
import com.spacecodee.securityspacee.session.application.response.ActiveSessionsResponse;

public interface IGetActiveSessionsUseCase {

    @NonNull
    ActiveSessionsResponse execute(@NonNull GetActiveSessionsQuery query);
}
