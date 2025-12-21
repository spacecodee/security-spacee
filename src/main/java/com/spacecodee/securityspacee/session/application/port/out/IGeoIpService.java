package com.spacecodee.securityspacee.session.application.port.out;

import java.util.Optional;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.Location;

public interface IGeoIpService {

    @NonNull
    Optional<Location> lookup(@NonNull String ipAddress);

    boolean isSuspiciousLocation(@NonNull Integer userId, @NonNull Location newLocation);
}
