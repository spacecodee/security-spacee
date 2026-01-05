package com.spacecodee.securityspacee.session.infrastructure.persistence.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringJpaSessionRepository extends JpaRepository<SessionEntity, String> {

    @NonNull
    Optional<SessionEntity> findBySessionToken(@NonNull String sessionToken);

    @NonNull
    List<SessionEntity> findByUserId(@NonNull Integer userId);

    @NonNull
    List<SessionEntity> findByUserIdAndIsActive(@NonNull Integer userId, @NonNull Boolean isActive);

    boolean existsByUserIdAndDeviceFingerprint(@NonNull Integer userId, @NonNull String deviceFingerprint);

    void deleteByUserId(@NonNull Integer userId);

    boolean existsBySessionToken(@NonNull String sessionToken);

    @Query("SELECT s FROM SessionEntity s WHERE s.lastActivityAt < :cutoff AND s.isActive = true")
    @NonNull
    List<SessionEntity> findInactiveSessions(@Param("cutoff") @NonNull Instant cutoff);

    @Query("SELECT s FROM SessionEntity s WHERE s.expiresAt < :now AND s.isActive = true")
    @NonNull
    List<SessionEntity> findAbsoluteExpired(@Param("now") @NonNull Instant now);
}
