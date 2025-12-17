package com.spacecodee.securityspacee.jwttoken.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.repository.IJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;
import com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.jpa.JwtTokenEntity;
import com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.jpa.SpringJpaJwtTokenRepository;
import com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.mapper.IJwtTokenPersistenceMapper;

public class JwtTokenPersistenceAdapter implements IJwtTokenRepository {

    private final SpringJpaJwtTokenRepository springJpaRepository;
    private final IJwtTokenPersistenceMapper mapper;

    public JwtTokenPersistenceAdapter(
            @NonNull SpringJpaJwtTokenRepository springJpaRepository,
            @NonNull IJwtTokenPersistenceMapper mapper) {
        this.springJpaRepository = springJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void save(@NonNull JwtToken token) {
        JwtTokenEntity entity = mapper.toEntity(token);
        JwtTokenEntity saved = springJpaRepository.save(entity);
        mapper.toDomain(saved);
    }

    @Override
    @NonNull
    @Transactional(readOnly = true)
    public Optional<JwtToken> findByJti(@NonNull Jti jti) {
        return springJpaRepository.findByJti(jti.getValue().toString())
                .map(mapper::toDomain);
    }

    @Override
    @NonNull
    @Transactional(readOnly = true)
    public @Unmodifiable List<JwtToken> findBySessionId(@NonNull String sessionId) {
        return springJpaRepository.findBySessionId(sessionId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @NonNull
    @Transactional(readOnly = true)
    public @Unmodifiable List<JwtToken> findByUserId(@NonNull Integer userId) {
        return springJpaRepository.findByUserIdAndState(userId, TokenState.ACTIVE).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void revokeAllBySessionId(@NonNull String sessionId) {
        springJpaRepository.revokeAllBySessionId(sessionId, TokenState.REVOKED);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(@NonNull Integer userId) {
        springJpaRepository.revokeAllByUserId(userId, TokenState.REVOKED);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByJtiAndState(@NonNull Jti jti, @NonNull TokenState state) {
        return springJpaRepository.findByJti(jti.getValue().toString())
                .map(entity -> entity.getState() == state)
                .orElse(false);
    }

    @Override
    @NonNull
    @Transactional(readOnly = true)
    public Optional<JwtToken> findLatestAccessTokenBySessionId(@NonNull String sessionId) {
        return this.springJpaRepository.findLatestAccessTokenBySessionId(sessionId)
                .map(this.mapper::toDomain);
    }
}
