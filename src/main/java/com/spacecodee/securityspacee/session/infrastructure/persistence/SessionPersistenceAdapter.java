package com.spacecodee.securityspacee.session.infrastructure.persistence;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.valueobject.DeviceFingerprint;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionState;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionToken;
import com.spacecodee.securityspacee.session.infrastructure.persistence.jpa.SessionEntity;
import com.spacecodee.securityspacee.session.infrastructure.persistence.jpa.SpringJpaSessionRepository;
import com.spacecodee.securityspacee.session.infrastructure.persistence.mapper.ISessionPersistenceMapper;

@Transactional
public class SessionPersistenceAdapter implements ISessionRepository {

    private final SpringJpaSessionRepository jpaRepository;
    private final ISessionPersistenceMapper mapper;

    public SessionPersistenceAdapter(
            @NonNull SpringJpaSessionRepository jpaRepository,
            @NonNull ISessionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public @NonNull Session save(@NonNull Session session) {
        SessionEntity entity = this.mapper.toEntity(session);
        SessionEntity savedEntity = this.jpaRepository.save(entity);
        return this.mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull Optional<Session> findById(@NonNull SessionId sessionId) {
        return this.jpaRepository.findById(sessionId.toString())
                .map(this.mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull Optional<Session> findBySessionToken(@NonNull SessionToken sessionToken) {
        return this.jpaRepository.findBySessionToken(sessionToken.toString())
                .map(this.mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull List<Session> findByUserId(@NonNull Integer userId) {
        return this.jpaRepository.findByUserId(userId).stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull List<Session> findByUserIdAndState(@NonNull Integer userId, @NonNull SessionState state) {
        boolean isActive = state == SessionState.ACTIVE;
        return this.jpaRepository.findByUserIdAndIsActive(userId, isActive).stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull List<Session> findActiveByUserId(@NonNull Integer userId) {
        return this.jpaRepository.findByUserIdAndIsActive(userId, true).stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull Optional<Session> findLatestByUserId(@NonNull Integer userId) {
        return this.jpaRepository.findByUserId(userId).stream()
                .map(this.mapper::toDomain)
                .max(Comparator.comparing(s -> s.getMetadata().getCreatedAt()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndFingerprint(@NonNull Integer userId, @NonNull DeviceFingerprint fingerprint) {
        return this.jpaRepository.existsByUserIdAndDeviceFingerprint(userId, fingerprint.value());
    }

    @Override
    public void deleteById(@NonNull SessionId sessionId) {
        this.jpaRepository.deleteById(sessionId.toString());
    }

    @Override
    public void deleteByUserId(@NonNull Integer userId) {
        this.jpaRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySessionToken(@NonNull SessionToken sessionToken) {
        return this.jpaRepository.existsBySessionToken(sessionToken.toString());
    }
}
