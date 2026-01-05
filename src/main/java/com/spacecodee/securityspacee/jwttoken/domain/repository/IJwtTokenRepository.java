package com.spacecodee.securityspacee.jwttoken.domain.repository;

import java.util.List;
import java.util.Optional;

import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;

public interface IJwtTokenRepository {

    void save(JwtToken token);

    Optional<JwtToken> findByJti(Jti jti);

    List<JwtToken> findBySessionId(String sessionId);

    List<JwtToken> findByUserId(Integer userId);

    void revokeAllBySessionId(String sessionId);

    void revokeAllByUserId(Integer userId);

    boolean existsByJtiAndState(Jti jti, TokenState state);

    Optional<JwtToken> findLatestAccessTokenBySessionId(String sessionId);
}
