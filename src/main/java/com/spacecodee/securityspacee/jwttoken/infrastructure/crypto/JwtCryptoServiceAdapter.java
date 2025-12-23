package com.spacecodee.securityspacee.jwttoken.infrastructure.crypto;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.application.port.out.IJwtCryptoService;
import com.spacecodee.securityspacee.jwttoken.domain.exception.InvalidSignatureException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.InvalidTokenException;
import com.spacecodee.securityspacee.shared.config.properties.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

public final class JwtCryptoServiceAdapter implements IJwtCryptoService {

    private static final String MESSAGE_KEY_PREFIX = "jwttoken.exception.";
    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    public JwtCryptoServiceAdapter(@NonNull JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
    }

    @Override
    @NonNull
    public String generateToken(@NonNull Map<String, Object> claims, @NonNull Duration expiration) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(expiration);

        // Extract JTI and subject from claims (they should be present)
        String jti = (String) claims.get("jti");
        String subject = (String) claims.get("sub");

        return Jwts.builder()
                .id(jti)
                .subject(subject)
                .claims(claims)
                .issuer(jwtProperties.issuer())
                .audience().add(jwtProperties.audience()).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .signWith(secretKey)
                .compact();
    }

    @Override
    @NonNull
    public @Unmodifiable Map<String, Object> parseToken(@NonNull String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(jwtProperties.issuer())
                    .requireAudience(jwtProperties.audience())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Map.copyOf(claims);
        } catch (SignatureException _) {
            throw new InvalidSignatureException(MESSAGE_KEY_PREFIX + "invalid_signature");
        } catch (ExpiredJwtException ex) {
            // Re-throw as-is, will be handled by ValidateTokenUseCase
            throw ex;
        } catch (JwtException _) {
            throw new InvalidTokenException(MESSAGE_KEY_PREFIX + "invalid_token");
        }
    }

    @Override
    public boolean validateSignature(@NonNull String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(jwtProperties.issuer())
                    .requireAudience(jwtProperties.audience())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException _) {
            return false;
        }
    }

    @Override
    @NonNull
    public Instant getExpirationDate(@NonNull String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            if (expiration == null) {
                throw new InvalidTokenException(MESSAGE_KEY_PREFIX + "invalid_token");
            }
            return expiration.toInstant();
        } catch (ExpiredJwtException ex) {
            // Even expired tokens have expiration date
            return ex.getClaims().getExpiration().toInstant();
        } catch (JwtException _) {
            throw new InvalidTokenException(MESSAGE_KEY_PREFIX + "invalid_token");
        }
    }

    @Override
    @NonNull
    public String getJtiFromToken(@NonNull String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String jti = claims.getId();
            if (jti == null || jti.isBlank()) {
                throw new InvalidTokenException(MESSAGE_KEY_PREFIX + "invalid_token");
            }
            return jti;
        } catch (ExpiredJwtException ex) {
            // Even expired tokens have JTI
            return ex.getClaims().getId();
        } catch (JwtException _) {
            throw new InvalidTokenException(MESSAGE_KEY_PREFIX + "invalid_token");
        }
    }
}
