package com.spacecodee.securityspacee.session.application.port.out;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.DeviceInfo;

public interface IUserAgentParser {

    @NonNull
    DeviceInfo parse(@NonNull String userAgent);
}
