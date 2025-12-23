package com.spacecodee.securityspacee.shared.application.port.out;

import org.jspecify.annotations.NonNull;

public interface IMessageResolverPort {

    @NonNull
    String getMessage(@NonNull String code);

    @NonNull
    String getMessage(@NonNull String code, Object... args);
}
