package com.spacecodee.securityspacee.session.infrastructure.service;

import java.util.Optional;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.port.out.IGeoIpService;
import com.spacecodee.securityspacee.session.domain.valueobject.Location;

public final class DisabledGeoIpService implements IGeoIpService {

    @Override
    public @NonNull Optional<Location> lookup(@NonNull String ipAddress) {
        return Optional.empty();
    }

    @Override
    public boolean isSuspiciousLocation(@NonNull Integer userId, @NonNull Location newLocation) {
        return false;
    }
}
