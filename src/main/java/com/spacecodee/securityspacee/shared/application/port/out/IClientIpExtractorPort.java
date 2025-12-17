package com.spacecodee.securityspacee.shared.application.port.out;

import org.jspecify.annotations.NonNull;

import jakarta.servlet.http.HttpServletRequest;

public interface IClientIpExtractorPort {

    @NonNull
    String extractClientIp(@NonNull HttpServletRequest request);
}
