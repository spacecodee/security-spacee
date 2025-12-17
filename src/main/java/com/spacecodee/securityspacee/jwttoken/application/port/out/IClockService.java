package com.spacecodee.securityspacee.jwttoken.application.port.out;

import java.time.Duration;
import java.time.Instant;

public interface IClockService {

    Instant now();

    Instant nowPlusDuration(Duration duration);
}
