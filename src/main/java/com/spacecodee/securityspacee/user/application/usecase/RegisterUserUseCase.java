package com.spacecodee.securityspacee.user.application.usecase;

import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.user.application.command.RegisterUserCommand;
import com.spacecodee.securityspacee.user.application.mapper.IUserResponseMapper;
import com.spacecodee.securityspacee.user.application.port.in.IRegisterUserUseCase;
import com.spacecodee.securityspacee.user.application.port.out.IPasswordEncoder;
import com.spacecodee.securityspacee.user.application.response.UserResponse;
import com.spacecodee.securityspacee.user.domain.event.UserRegisteredEvent;
import com.spacecodee.securityspacee.user.domain.exception.DuplicateEmailException;
import com.spacecodee.securityspacee.user.domain.exception.DuplicateUsernameException;
import com.spacecodee.securityspacee.user.domain.model.User;
import com.spacecodee.securityspacee.user.domain.model.UserProfile;
import com.spacecodee.securityspacee.user.domain.model.UserProfileSnapshot;
import com.spacecodee.securityspacee.user.domain.repository.IUserRepository;
import com.spacecodee.securityspacee.user.domain.valueobject.Email;
import com.spacecodee.securityspacee.user.domain.valueobject.Password;
import com.spacecodee.securityspacee.user.domain.valueobject.Username;

public class RegisterUserUseCase implements IRegisterUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterUserUseCase.class);

    private final IUserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;
    private final IUserResponseMapper responseMapper;
    private final ApplicationEventPublisher eventPublisher;

    public RegisterUserUseCase(
            IUserRepository userRepository,
            IPasswordEncoder passwordEncoder,
            IUserResponseMapper responseMapper,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
        this.responseMapper = Objects.requireNonNull(responseMapper);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    @Transactional
    public UserResponse register(RegisterUserCommand command) {
        Objects.requireNonNull(command);

        log.info("Starting user registration for username: {}", command.username());

        validateUniqueness(command.username(), command.email());

        Username username = Username.of(command.username());
        Email email = Email.of(command.email());
        Password plainPassword = Password.ofPlain(command.password());

        String hashedPasswordValue = passwordEncoder.encode(plainPassword.getValue());
        Password hashedPassword = Password.ofHashed(hashedPasswordValue);

        UserProfile profile = null;
        if (command.userType().requiresProfile()) {
            UserProfileSnapshot profileSnapshot = new UserProfileSnapshot(
                    command.firstName(),
                    command.lastName(),
                    command.phoneNumber(),
                    false,
                    command.languageCode(),
                    command.avatarUrl(),
                    command.bio(),
                    command.timezone(),
                    command.dateOfBirth());
            profile = UserProfile.of(profileSnapshot);
        }

        User user = User.create(username, email, hashedPassword, command.userType(), profile);
        User savedUser = userRepository.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getUserId());

        this.publishUserRegisteredEvent(savedUser);

        return responseMapper.toResponse(savedUser);
    }

    private void validateUniqueness(String usernameValue, String emailValue) {
        Username username = Username.of(usernameValue);
        Email email = Email.of(emailValue);

        if (userRepository.existsByUsername(username)) {
            log.warn("Registration failed: Username '{}' already exists", usernameValue);
            throw new DuplicateUsernameException(usernameValue);
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed: Email '{}' already exists", emailValue);
            throw new DuplicateEmailException(emailValue);
        }
    }

    private void publishUserRegisteredEvent(@NonNull User user) {
        UserRegisteredEvent event = new UserRegisteredEvent(
                user.getUserId(),
                user.getUsername().getValue(),
                user.getEmail().getValue(),
                user.getUserType());

        eventPublisher.publishEvent(event);
        log.debug("Published UserRegisteredEvent for userId: {}", user.getUserId());
    }
}
