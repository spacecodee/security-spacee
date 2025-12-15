package com.spacecodee.securityspacee.user.infrastructure.persistence;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spacecodee.securityspacee.user.domain.exception.DuplicateEmailException;
import com.spacecodee.securityspacee.user.domain.exception.DuplicateUsernameException;
import com.spacecodee.securityspacee.user.domain.model.User;
import com.spacecodee.securityspacee.user.domain.repository.IUserRepository;
import com.spacecodee.securityspacee.user.domain.valueobject.Email;
import com.spacecodee.securityspacee.user.domain.valueobject.Username;
import com.spacecodee.securityspacee.user.infrastructure.persistence.jpa.SpringJpaUserRepository;
import com.spacecodee.securityspacee.user.infrastructure.persistence.jpa.UserAuthEntity;
import com.spacecodee.securityspacee.user.infrastructure.persistence.mapper.IUserPersistenceMapper;

public final class UserPersistenceAdapter implements IUserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserPersistenceAdapter.class);

    private final SpringJpaUserRepository jpaRepository;
    private final IUserPersistenceMapper mapper;

    public UserPersistenceAdapter(
            SpringJpaUserRepository jpaRepository,
            IUserPersistenceMapper mapper) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public User save(User user) {
        Objects.requireNonNull(user);

        if (user.getUserId() == null) {
            if (existsByUsername(user.getUsername())) {
                throw new DuplicateUsernameException(user.getUsername().getValue());
            }
            if (existsByEmail(user.getEmail())) {
                throw new DuplicateEmailException(user.getEmail().getValue());
            }
        }

        log.debug("Persisting User: {}", user.getUsername().getValue());

        UserAuthEntity entity = mapper.toEntity(user);
        UserAuthEntity savedEntity = jpaRepository.save(entity);

        log.debug("User persisted with ID: {}", savedEntity.getUserId());
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long userId) {
        Objects.requireNonNull(userId);

        return jpaRepository.findById(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        Objects.requireNonNull(username);

        return jpaRepository.findByUsernameIgnoreCase(username.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        Objects.requireNonNull(email);

        return jpaRepository.findByEmailIgnoreCase(email.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(Username username) {
        Objects.requireNonNull(username);

        return jpaRepository.existsByUsernameIgnoreCase(username.getValue());
    }

    @Override
    public boolean existsByEmail(Email email) {
        Objects.requireNonNull(email);

        return jpaRepository.existsByEmailIgnoreCase(email.getValue());
    }

    @Override
    public void deleteById(Long userId) {
        Objects.requireNonNull(userId);

        log.warn("Hard delete requested for userId: {}", userId);
        jpaRepository.deleteById(userId);
    }
}
