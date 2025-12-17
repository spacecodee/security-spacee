package com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;

public interface SpringJpaJwtTokenRepository extends JpaRepository<JwtTokenEntity, Long> {

    @NonNull
    Optional<JwtTokenEntity> findByJti(@NonNull String jti);

    @NonNull
    List<JwtTokenEntity> findBySessionId(@NonNull String sessionId);

    @NonNull
    List<JwtTokenEntity> findByUserIdAndState(@NonNull Integer userId, @NonNull TokenState state);

    @Modifying
    @Query("UPDATE JwtTokenEntity j SET j.state = :state WHERE j.sessionId = :sessionId AND j.state = com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState.ACTIVE")
    void revokeAllBySessionId(@Param("sessionId") String sessionId, @Param("state") TokenState state);

    @Modifying
    @Query("UPDATE JwtTokenEntity j SET j.state = :state WHERE j.userId = :userId AND j.state = com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState.ACTIVE")
    void revokeAllByUserId(@Param("userId") Integer userId, @Param("state") TokenState state);

    @NonNull
    @Query("SELECT j FROM JwtTokenEntity j WHERE j.sessionId = :sessionId AND j.tokenType = com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenType.ACCESS ORDER BY j.issuedAt DESC LIMIT 1")
    Optional<JwtTokenEntity> findLatestAccessTokenBySessionId(@Param("sessionId") String sessionId);
}
