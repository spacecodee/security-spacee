package com.spacecodee.securityspacee.jwttoken.infrastructure.clock;

import java.time.Duration;
import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.application.port.out.IClockService;

public final class ClockServiceAdapter implements IClockService {

    @Override
    @NonNull
    public Instant now() {
        return Instant.now();
    }

    @Override
    @NonNull
    public Instant nowPlusDuration(@NonNull Duration duration) {
        return Instant.now().plus(duration);
    }
}
