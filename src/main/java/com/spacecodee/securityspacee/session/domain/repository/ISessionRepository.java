package com.spacecodee.securityspacee.session.domain.repository;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.valueobject.DeviceFingerprint;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionState;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionToken;

public interface ISessionRepository {

    @NonNull
    Session save(@NonNull Session session);

    @NonNull
    Optional<Session> findById(@NonNull SessionId sessionId);

    @NonNull
    Optional<Session> findBySessionToken(@NonNull SessionToken sessionToken);

    @NonNull
    List<Session> findByUserId(@NonNull Integer userId);

    @NonNull
    List<Session> findByUserIdAndState(@NonNull Integer userId, @NonNull SessionState state);

    @NonNull
    List<Session> findActiveByUserId(@NonNull Integer userId);

    @NonNull
    Optional<Session> findLatestByUserId(@NonNull Integer userId);

    boolean existsByUserIdAndFingerprint(@NonNull Integer userId, @NonNull DeviceFingerprint fingerprint);

    void deleteById(@NonNull SessionId sessionId);

    void deleteByUserId(@NonNull Integer userId);

    boolean existsBySessionToken(@NonNull SessionToken sessionToken);
}
