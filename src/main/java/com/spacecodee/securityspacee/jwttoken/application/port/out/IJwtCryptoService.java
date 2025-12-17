package com.spacecodee.securityspacee.jwttoken.application.port.out;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public interface IJwtCryptoService {

    String generateToken(Map<String, Object> claims, Duration expiration);

    Map<String, Object> parseToken(String token);

    boolean validateSignature(String token);

    Instant getExpirationDate(String token);

    String getJtiFromToken(String token);
}
