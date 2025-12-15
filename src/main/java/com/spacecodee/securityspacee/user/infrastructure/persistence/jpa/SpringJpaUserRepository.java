package com.spacecodee.securityspacee.user.infrastructure.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

@Repository
public interface SpringJpaUserRepository extends JpaRepository<UserAuthEntity, Long> {

    Optional<UserAuthEntity> findByUsernameIgnoreCase(String username);

    Optional<UserAuthEntity> findByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    Optional<UserAuthEntity> findByUsernameIgnoreCaseAndUserType(String username, UserType userType);
}
